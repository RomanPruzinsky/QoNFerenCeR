package tr.qonferencer.backend.content

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import tr.qonferencer.backend.meal.MealReservationRepository
import tr.qonferencer.backend.meal.MealWindowRepository
import tr.qonferencer.backend.meal.toDto
import tr.qonferencer.backend.meal.toUserMealEntry
import tr.qonferencer.backend.n8n.OutboundEvent
import tr.qonferencer.backend.n8n.OutboundEvents
import tr.qonferencer.backend.user.CallerService
import tr.qonferencer.backend.user.User
import tr.qonferencer.backend.user.UserAnchorService
import tr.qonferencer.shared.dtos.SplashDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.enums.Role

/** Builds the splash aggregate (languages + translations + role-filtered screen menu + meal windows + self profile) */
@Service
class SplashService(
	private val languages: LanguageRepository,
	private val translations: TranslationRepository,
	private val screens: CustomScreenRepository,
	private val windows: MealWindowRepository,
	private val reservations: MealReservationRepository,
	private val anchors: UserAnchorService,
	private val caller: CallerService,
	private val events: OutboundEvents,
) {
	/** Assembles everything the app needs at start; the launch event fires before the ETag check */
	fun build(): SplashDto {
		val role = caller.role()
		val me = caller.userOrNull()?.let { buildMe(it, role) }
		events.publish(OutboundEvent.AppLaunched(user = me))
		return SplashDto(
			languages = languages.findAll(Sort.by("code")).map { it.toDto() },
			translations = translations.findAll(Sort.by("id.key", "id.langCode")).map { it.toDto() },
			customScreens = screens.findAll(Sort.by("id")).filter { role.atLeast(it.minRole) }.map { it.toDto() },
			mealWindows = windows.findAll(Sort.by("startsAt")).map { it.toDto() },
			me = me,
		)
	}

	/** Role/isSpeaker/canCheckByName come free off the caller's own JWT */
	private fun buildMe(user: User, role: Role) = UserDetailDto(
		userId = user.id,
		fullName = user.fullName,
		role = role,
		isSpeaker = caller.isSpeaker(),
		canCheckByName = caller.canCheckByName(),
		customData = anchors.customData(user),
		meals = reservations.findByIdUserId(user.id).map { it.toUserMealEntry() },
	)
}
