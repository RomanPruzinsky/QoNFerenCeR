package tr.qonferencer.backend.meal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.backend.user.CallerService
import tr.qonferencer.shared.dtos.MealCountDto
import tr.qonferencer.shared.enums.Role

@Service
class MealCountsService(
	private val reservations: MealReservationRepository,
	private val caller: CallerService,
) {
	@Transactional(readOnly = true)
	fun counts(windowId: Long): List<MealCountDto> {
		val allowed = caller.role().atLeast(Role.VOLUNTEER) && caller.canFoodCheck()
		if (!allowed) throw forbidden("needs VOLUNTEER with canFoodCheck")

		return reservations.remainingByWindow(windowId).map { MealCountDto(it.getVariantKey(), it.getRemaining().toInt()) }
	}
}
