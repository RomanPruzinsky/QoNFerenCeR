package tr.qonferencer.shared.dtos

/**
 * Data loaded at app's start
 * @property languages Available languages
 * @property translations Translation entries for all keys
 * @property customScreens List of custom screens to draw
 */
data class SplashDto(
	val languages: List<LanguageDto> = emptyList(),
	val translations: List<TranslationDto> = emptyList(),
	val customScreens: List<CustomScreenDto> = emptyList(),
)
