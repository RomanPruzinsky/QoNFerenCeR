package tr.qonferencer.backend.content

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import tr.qonferencer.backend.user.CallerService
import tr.qonferencer.shared.dtos.SplashDto

/** Builds the splash aggregate (languages + translations + role-filtered screen menu) */
@Service
class SplashService(
	private val languages: LanguageRepository,
	private val translations: TranslationRepository,
	private val screens: CustomScreenRepository,
	private val caller: CallerService,
) {
	fun build(): SplashDto {
		val role = caller.activeRole()
		return SplashDto(
			languages = languages.findAll(Sort.by("code")).map { it.toDto() },
			translations = translations.findAll(Sort.by("id.key", "id.langCode")).map { it.toDto() },
			customScreens = screens.findAll(Sort.by("id")).filter { role.atLeast(it.minRole) }.map { it.toDto() },
		)
	}
}
