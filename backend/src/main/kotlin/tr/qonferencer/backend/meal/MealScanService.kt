package tr.qonferencer.backend.meal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.shared.dtos.MealScanResultDto
import tr.qonferencer.shared.enums.MealScanResult

/** Meal scan domain logic: 1 meal per window, reservation presence = registered */
@Service
class MealScanService(
	private val reservations: MealReservationRepository,
	private val consumptions: MealConsumptionRepository,
) {
	/** Records a meal for [userId] in [windowId], scanned by [scannedBy] */
	@Transactional
	fun scan(userId: Long, windowId: Long, scannedBy: Long?): MealScanResultDto {
		val slot = MealSlotId(userId, windowId)
		val reservation = reservations.findById(slot).orElse(null)
			?: return MealScanResultDto(MealScanResult.NOT_REGISTERED_PORTION, userId, null)
		val approved = consumptions.consume(userId, windowId, scannedBy)
		val result = if (approved) MealScanResult.APPROVED else MealScanResult.ALREADY_CONSUMED
		return MealScanResultDto(result, userId, reservation.variantKey)
	}
}
