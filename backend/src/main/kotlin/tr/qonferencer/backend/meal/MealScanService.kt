package tr.qonferencer.backend.meal

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.backend.n8n.OutboundEvent
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

@Service
class MealScanService(
	private val reservations: MealReservationRepository,
	private val consumptions: MealConsumptionRepository,
	private val users: UserRepository,
	private val caller: CallerService,
	private val events: ApplicationEventPublisher,
) {
	@Transactional
	fun scan(request: MealScanRequestDto): MealScanResultDto {
		if (!caller.role().atLeast(Role.VOLUNTEER)) throw forbidden("role below VOLUNTEER")
		
		val windowId = request.mealWindowId
		val scannedBy = caller.requireUserId()
		val scannerType = request.scannerType
		
		val userId = verify(request.token, scannerType)
			?: return denied(null, windowId, scannedBy, scannerType, MealScanResult.NO_USER_FOUND)
		
		val mealSlotId = MealSlotId(userId, windowId)
		val reservation = reservations.findById(mealSlotId).orElse(null)
			?: return denied(userId, windowId, scannedBy, scannerType, MealScanResult.NOT_REGISTERED_PORTION)
		
		when (consumptions.consume(mealSlotId, scannedBy, request.idempotencyKey)) {
			ConsumeOutcome.CONFLICT -> return denied(
				userId = userId,
				windowId = windowId,
				scannedBy = scannedBy,
				scannerType = scannerType,
				result = MealScanResult.ALREADY_CONSUMED,
			)
			
			ConsumeOutcome.NEW -> events.publishEvent(
				OutboundEvent.MealApproved(
					userId = userId,
					meal = UserMealEntryDto(windowId, reservation.variantKey),
					scannedBy = scannedBy,
					scannerType = scannerType,
				),
			)
			
			ConsumeOutcome.RETRY -> Unit
		}
		
		return MealScanResultDto(MealScanResult.APPROVED, reservation.variantKey)
	}

	/** @return Verified `app_user.id`, or null if user not found */
	private fun verify(token: String, scannerType: ScannerType, now: Instant = Instant.now()): Long? = when (scannerType) {
		ScannerType.QR, ScannerType.NFC -> {
			val parsed = ScanToken.parse(token) ?: return null
			val secret = users.findById(parsed.userId).orElse(null)?.mealSecret ?: return null
			if (!ScanToken.matches(parsed, secret, now.epochSecond)) return null
			parsed.userId
		}
			
		ScannerType.BARCODE, ScannerType.MANUAL -> {
			val userId = token.trim().toLongOrNull() ?: return null
			if (users.existsById(userId)) userId else null
		}
	}
	
	private fun denied(
		userId: Long?,
		windowId: Long,
		scannedBy: Long,
		scannerType: ScannerType,
		result: MealScanResult,
	): MealScanResultDto {
		events.publishEvent(
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
