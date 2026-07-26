package tr.qonferencer.backend.content

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import tr.qonferencer.backend.meal.MealWindowRepository
import tr.qonferencer.backend.meal.toDto
import tr.qonferencer.backend.n8n.EventType
import tr.qonferencer.backend.n8n.OutboundEvents
import tr.qonferencer.backend.user.CallerService
import tr.qonferencer.shared.dtos.SplashDto

/** Builds the splash aggregate (languages + translations + role-filtered screen menu + meal windows) */
@Service
class SplashService(
	private val languages: LanguageRepository,
	private val translations: TranslationRepository,
	private val screens: CustomScreenRepository,
	private val windows: MealWindowRepository,
	private val caller: CallerService,
	private val events: OutboundEvents,
) {
	/** Assembles everything the app needs at start; the launch event fires before the ETag check */
	fun build(): SplashDto {
		val role = caller.activeRole()
		events.publish(EventType.APP_LAUNCHED, mapOf("role" to role.name, "isSpeaker" to caller.activeIsSpeaker()))
		return SplashDto(
			languages = languages.findAll(Sort.by("code")).map { it.toDto() },
			translations = translations.findAll(Sort.by("id.key", "id.langCode")).map { it.toDto() },
			customScreens = screens.findAll(Sort.by("id")).filter { role.atLeast(it.minRole) }.map { it.toDto() },
			mealWindows = windows.findAll(Sort.by("startsAt")).map { it.toDto() },
		)
	}
}
