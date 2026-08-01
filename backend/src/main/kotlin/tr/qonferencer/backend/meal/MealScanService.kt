package tr.qonferencer.backend.meal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.backend.n8n.OutboundEvent
import tr.qonferencer.backend.n8n.OutboundEvents
import tr.qonferencer.backend.user.CallerService
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.dtos.MealScanRequestDto
import tr.qonferencer.shared.dtos.MealScanResultDto
import tr.qonferencer.shared.dtos.UserMealEntryDto
import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.shared.enums.ScannerType
import tr.qonferencer.shared.scan.ScanToken
import java.time.Instant

/** Meal scan domain logic: 1 meal per window, reservation presence = registered */
@Service
class MealScanService(
	private val reservations: MealReservationRepository,
	private val consumptions: MealConsumptionRepository,
	private val users: UserRepository,
	private val caller: CallerService,
	private val events: OutboundEvents,
) {
	/** Serves a scanner request; every domain verdict is a result, only authorization is an error */
	@Transactional
	fun scan(request: MealScanRequestDto): MealScanResultDto {
		if (!caller.role().atLeast(Role.VOLUNTEER)) throw forbidden("role below VOLUNTEER")
		
		val scannedBy = caller.requireUserId()
		val windowId = request.mealWindowId
		val scannerType = request.scannerType
		val userId = verify(request.token, scannerType)
			?: return denied(null, windowId, scannedBy, scannerType, MealScanResult.NO_USER_FOUND)
		val slot = MealSlotId(userId, windowId)
		val reservation = reservations.findById(slot).orElse(null)
			?: return denied(userId, windowId, scannedBy, scannerType, MealScanResult.NOT_REGISTERED_PORTION)
		val isNewConsumption = consumptions.consume(slot, scannedBy, request.idempotencyKey)
		val isRetry = !isNewConsumption &&
			consumptions.findById(slot).orElse(null)?.idempotencyKey == request.idempotencyKey
		if (!isNewConsumption && !isRetry) {
			return denied(userId, windowId, scannedBy, scannerType, MealScanResult.ALREADY_CONSUMED)
		}
		if (isNewConsumption) {
			events.publish(
				OutboundEvent.MealApproved(
					userId = userId,
					meal = UserMealEntryDto(windowId, reservation.variantKey),
					scannedBy = scannedBy,
					scannerType = scannerType,
				),
			)
		}
		return MealScanResultDto(MealScanResult.APPROVED, reservation.variantKey)
	}

	/** @return Verified `app_user.id`, or null */
	private fun verify(token: String, scannerType: ScannerType, now: Instant = Instant.now()): Long? = when (scannerType) {
		ScannerType.QR, ScannerType.NFC -> {
			val parsed = ScanToken.parse(token) ?: return null
			val secret = users.findById(parsed.userId).orElse(null)?.qrSecret ?: return null
			if (!ScanToken.matches(parsed, secret, now.epochSecond)) return null
			parsed.userId
		}
			
		ScannerType.BARCODE, ScannerType.MANUAL -> {
			val userId = token.trim().toLongOrNull() ?: return null
			if (users.existsById(userId)) userId else null
		}
	}

	/** Answers [result] and tells the organizer; a refused scan leaves no database trace */
	private fun denied(
		userId: Long?,
		windowId: Long,
		scannedBy: Long,
		scannerType: ScannerType,
		result: MealScanResult,
	): MealScanResultDto {
		events.publish(
			OutboundEvent.MealDenied(
				userId = userId,
				windowId = windowId,
				reason = result,
				scannedBy = scannedBy,
				scannerType = scannerType,
			),
		)
		return MealScanResultDto(result, null)
	}
}
