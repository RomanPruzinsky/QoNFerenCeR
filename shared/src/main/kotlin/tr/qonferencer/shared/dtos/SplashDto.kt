package tr.qonferencer.shared.dtos

/**
 * Data loaded at app's start
 * @property languages Available languages
 * @property translations Translation entries for all keys
 * @property customScreens List of custom screens to draw
 * @property mealWindows Meal serving windows (referenced by user's meal plan)
 * @property me Caller's own profile, present only when the request carries valid JWT
 */
data class SplashDto(
	val languages: List<LanguageDto> = emptyList(),
	val translations: List<TranslationDto> = emptyList(),
	val customScreens: List<CustomScreenDto> = emptyList(),
	val mealWindows: List<MealWindowDto> = emptyList(),
	val me: UserDetailDto? = null,
)
