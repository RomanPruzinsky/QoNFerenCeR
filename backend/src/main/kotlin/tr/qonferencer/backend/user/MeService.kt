package tr.qonferencer.backend.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.meal.MealReservationRepository
import tr.qonferencer.backend.meal.toUserMealEntry
import tr.qonferencer.shared.dtos.MeDto
import java.util.Base64

@Service
class MeService(
	private val users: UserRepository,
	private val caller: CallerService,
	private val anchors: UserAnchorService,
	private val reservations: MealReservationRepository,
) {
	@Transactional
	fun me(): MeDto {
		val user = caller.requireAppUser()
		return MeDto(
			userId = user.id,
			role = caller.role(),
			isSpeaker = caller.isSpeaker(),
			consented = user.consented,
			qrSecret = Base64.getEncoder().encodeToString(user.qrSecret),
			customData = anchors.customData(user),
			meals = reservations.findByIdUserId(user.id).map { it.toUserMealEntry() },
		)
	}

	@Transactional
	fun consent() {
		val user = caller.requireAppUser()
		user.consented = true
		users.save(user)
	}
}
