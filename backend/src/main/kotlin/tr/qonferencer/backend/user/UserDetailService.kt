package tr.qonferencer.backend.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.admin.KeycloakAdminService
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.backend.common.notFound
import tr.qonferencer.backend.meal.MealReservationRepository
import tr.qonferencer.backend.meal.toUserMealEntry
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.enums.Role

@Service
class UserDetailService(
	private val users: UserRepository,
	private val anchors: UserAnchorService,
	private val reservations: MealReservationRepository,
	private val kc: KeycloakAdminService,
	private val caller: CallerService,
) {
	@Transactional(readOnly = true)
	fun detail(userId: Long): UserDetailDto {
		val role = caller.role()
		val allowed = role == Role.ADMIN || (role.atLeast(Role.ORGANISER) && caller.canCheckUsers())
		if (!allowed) throw forbidden("needs ORGANISER with canCheckUsers, or ADMIN")
		
		val user = users.findById(userId).orElseThrow { notFound("user $userId doesn't exist") }
		val info = kc.info(user.kcSub)
		return UserDetailDto(
			userId = user.id,
			fullName = user.fullName,
			role = info.role,
			isSpeaker = info.isSpeaker,
			canCheckUsers = info.canCheckUsers,
			canFoodCheck = info.canFoodCheck,
			customData = anchors.customData(user),
			meals = reservations.findByIdUserId(user.id).map { it.toUserMealEntry() },
		)
	}
}
