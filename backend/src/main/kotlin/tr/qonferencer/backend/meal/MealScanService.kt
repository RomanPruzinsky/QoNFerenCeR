package tr.qonferencer.backend.meal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.common.badRequest
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.backend.n8n.EventType
import tr.qonferencer.backend.n8n.OutboundEvents
import tr.qonferencer.backend.user.CallerService
import tr.qonferencer.shared.dtos.MealScanRequestDto
import tr.qonferencer.shared.dtos.MealScanResultDto
import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.shared.enums.ScanCarrier

/** Meal scan domain logic: 1 meal per window, reservation presence = registered */
@Service
class MealScanService(
	private val reservations: MealReservationRepository,
	private val consumptions: MealConsumptionRepository,
	private val tokens: MealScanTokenService,
	private val caller: CallerService,
	private val events: OutboundEvents,
) {
	/** Serves a scanner request; every domain verdict is a result, only authorization is an error */
	@Transactional
	fun scan(request: MealScanRequestDto): MealScanResultDto {
		if (!caller.role().atLeast(Role.VOLUNTEER)) throw forbidden("role below VOLUNTEER")
		val scannedBy = caller.appUserId()
		val windowId = request.mealWindowId
		val carrier = request.carrier
		val verified = tokens.verify(request.token)
			?: return denied(null, windowId, scannedBy, carrier, MealScanResult.NO_USER_FOUND)
		if (carrier.isRotating != verified.rotating) {
			throw badRequest("carrier $carrier does not match the scanned token")
		}
		val userId = verified.userId
		val slot = MealSlotId(userId, windowId)
		val reservation = reservations.findById(slot).orElse(null)
			?: return denied(userId, windowId, scannedBy, carrier, MealScanResult.NOT_REGISTERED_PORTION)
		val approved = consumptions.consume(slot, scannedBy, request.idempotencyKey) ||
			consumptions.findById(slot).orElse(null)?.idempotencyKey == request.idempotencyKey
		if (!approved) return denied(userId, windowId, scannedBy, carrier, MealScanResult.ALREADY_CONSUMED)
		events.publish(
			EventType.MEAL_APPROVED,
			mapOf(
				"userId" to userId,
				"windowId" to windowId,
				"variantKey" to reservation.variantKey,
				"scannedBy" to scannedBy,
				"carrier" to carrier.name,
			),
		)
		return MealScanResultDto(MealScanResult.APPROVED, userId, reservation.variantKey)
	}

	/** Answers [result] and tells the organizer; a refused scan leaves no database trace */
	private fun denied(
		userId: Long?,
		windowId: Long,
		scannedBy: Long,
		carrier: ScanCarrier,
		result: MealScanResult,
	): MealScanResultDto {
		events.publish(
			EventType.MEAL_DENIED,
			mapOf(
				"userId" to userId,
				"windowId" to windowId,
				"reason" to result.name,
				"scannedBy" to scannedBy,
				"carrier" to carrier.name,
			),
		)
		return MealScanResultDto(result, userId, null)
	}
}
