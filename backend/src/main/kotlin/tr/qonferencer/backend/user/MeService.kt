package tr.qonferencer.backend.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.meal.MealReservationRepository
import tr.qonferencer.backend.meal.toUserMealEntry
import tr.qonferencer.shared.dtos.MeDto
import java.util.Base64

@Service
class MeService(
	private val caller: CallerService,
	private val anchors: UserAnchorService,
	private val reservations: MealReservationRepository,
) {
	@Transactional(readOnly = true)
	fun me(): MeDto {
		val user = caller.requireAppUser()
		return MeDto(
			userId = user.id,
			fullName = user.fullName,
			role = caller.role(),
			isSpeaker = caller.isSpeaker(),
			qrSecret = Base64.getEncoder().encodeToString(user.qrSecret),
			customData = anchors.customData(user),
			meals = reservations.findByIdUserId(user.id).map { it.toUserMealEntry() },
		)
	}
}
