package tr.qonferencer.backend.content

import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import tr.qonferencer.backend.meal.MealReservationRepository
import tr.qonferencer.backend.meal.MealWindowRepository
import tr.qonferencer.backend.meal.toDto
import tr.qonferencer.backend.meal.toUserMealEntry
import tr.qonferencer.backend.n8n.OutboundEvent
import tr.qonferencer.backend.user.CallerService
import tr.qonferencer.backend.user.UserAnchorService
import tr.qonferencer.shared.dtos.SplashDto
import tr.qonferencer.shared.dtos.UserDetailDto

/** Builds splash data */
@Service
class SplashService(
	private val languages: LanguageRepository,
	private val translations: TranslationRepository,
	private val screens: CustomScreenRepository,
	private val windows: MealWindowRepository,
	private val reservations: MealReservationRepository,
	private val anchors: UserAnchorService,
	private val caller: CallerService,
	private val events: ApplicationEventPublisher,
) {
	/** Provides everything app needs at start */
	fun build(): SplashDto {
		val meRole = caller.role()
		val me = caller.userOrNull()?.let { user ->
			UserDetailDto(
				userId = user.id,
				fullName = user.fullName,
				role = meRole,
				isSpeaker = caller.isSpeaker(),
				canCheckByName = caller.canCheckByName(),
				customData = anchors.customData(user),
				meals = reservations.findByIdUserId(user.id).map { it.toUserMealEntry() },
			)
		}
		
		events.publishEvent(OutboundEvent.AppLaunched(user = me))
		
		return SplashDto(
			languages = languages.findAll(Sort.by("code")).map { it.toDto() },
			translations = translations.findAll(Sort.by("id.key", "id.langCode")).map { it.toDto() },
			customScreens = screens.findAll(Sort.by("id")).filter { meRole.atLeast(it.minRole) }.map { it.toDto() },
			mealWindows = windows.findAll(Sort.by("startsAt")).map { it.toDto() },
			me = me,
		)
	}
}
