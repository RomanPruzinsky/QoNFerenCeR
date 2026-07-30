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

/** Full profile of an attendee other than the caller; the info-desk detail view after a search */
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
		val allowed = caller.role().atLeast(Role.ORGANISER) && caller.canCheckByName()
		if (!allowed) throw forbidden("needs ORGANISER and canCheckByName")
		val user = users.findById(userId).orElseThrow { notFound("app_user $userId does not exist") }
		val info = kc.info(user.kcSub)
		return UserDetailDto(
			userId = user.id,
			fullName = user.fullName,
			username = info.username,
			role = info.role,
			isSpeaker = info.isSpeaker,
			canCheckByName = info.canCheckByName,
			customData = anchors.customData(user),
			meals = reservations.findByIdUserId(user.id).map { it.toUserMealEntry() },
		)
	}
}
