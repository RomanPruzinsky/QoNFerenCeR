package tr.qonferencer.shared.dtos

/**
 * Data loaded at app's start
 * @property translations Languages + translation entries for all keys
 * @property customScreens List of custom screens to draw
 * @property mealWindows Meal windows
 * @property me Caller's own profile (if requested/provided)
 */
data class SplashDto(
	val translations: AllTranslationsDto = AllTranslationsDto(emptyList(), emptyList()),
	val customScreens: List<CustomScreenDto> = emptyList(),
	val mealWindows: List<MealWindowDto> = emptyList(),
	val me: UserDetailDto? = null,
)
