package tr.qonferencer.shared.dtos

/**
 * Data loaded at app's start
 * @property languages Available languages
 * @property translations Translation entries for all keys
 * @property customScreens List of custom screens to draw
 * @property mealWindows Meal windows
 * @property me Caller's own profile (if requested/provided)
 */
data class SplashDto(
	val languages: List<LanguageDto> = emptyList(),
	val translations: List<TranslationDto> = emptyList(),
	val customScreens: List<CustomScreenDto> = emptyList(),
	val mealWindows: List<MealWindowDto> = emptyList(),
	val me: UserDetailDto? = null,
)
