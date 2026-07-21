package tr.qonferencer.backend.meal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.backend.user.CallerService
import tr.qonferencer.shared.dtos.MealScanRequestDto
import tr.qonferencer.shared.dtos.MealScanResultDto
import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.Role

/** Meal scan domain logic: 1 meal per window, reservation presence = registered */
@Service
class MealScanService(
	private val reservations: MealReservationRepository,
	private val consumptions: MealConsumptionRepository,
	private val tokens: MealScanTokenService,
	private val caller: CallerService,
) {
	/**
	 * Serves a scanner request; every domain verdict is a result, only authorization is an error
	 *
	 * A repeated `idempotencyKey` repeats the verdict instead of reporting a second meal.
	 */
	@Transactional
	fun scan(request: MealScanRequestDto): MealScanResultDto {
		if (!caller.role().atLeast(Role.VOLUNTEER)) throw forbidden("role below VOLUNTEER")
		val scannedBy = caller.appUserId()
		val userId = tokens.verify(request.token)
			?: return MealScanResultDto(MealScanResult.NO_USER_FOUND, null, null)
		val slot = MealSlotId(userId, request.mealWindowId)
		val reservation = reservations.findById(slot).orElse(null)
			?: return MealScanResultDto(MealScanResult.NOT_REGISTERED_PORTION, userId, null)
		val approved = consumptions.consume(slot, scannedBy, request.idempotencyKey) ||
			consumptions.findById(slot).orElse(null)?.idempotencyKey == request.idempotencyKey
		val result = if (approved) MealScanResult.APPROVED else MealScanResult.ALREADY_CONSUMED
		return MealScanResultDto(result, userId, reservation.variantKey)
	}
}
