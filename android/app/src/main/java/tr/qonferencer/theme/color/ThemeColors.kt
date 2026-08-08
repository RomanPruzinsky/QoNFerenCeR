package tr.qonferencer.theme.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

//////////////////////////////////
////////////// MAIN //////////////

/** Selectable app palette, persisted by `ThemePrefs` */
enum class AppColors(
	val colors: ThemeColors,
) {
	NS_BLUE(nsBlueThemeColors),
	MIDNIGHT_BLUE(midnightBlueThemeColors),
	SLATE_STEEL(slateSteelThemeColors),
	SAKURA_NIGHT(sakuraNightThemeColors),
	
	AURORA(auroraThemeColors),
	MOCHA_COFFEE(mochaCoffeeThemeColors),
	DEEP_SEA(deepSeaThemeColors),
	
	BLACK_PINK(blackPinkThemeColors),
	DEEP_ORANGE(deepOrangeThemeColors),
	TR(trThemeColors),
	SOLARIZED(solarizedThemeColors),
	FOREST_GREEN(forestGreenThemeColors),
}

/** Full color palette of one [AppColors] */
data class ThemeColors(
	val appBackground: Color,
	val container: Color,
	val element: Color,
	val clickable: Color,
	val selected: Color,
	
	val text: Color,
	
	val navigation: Color,
	
	val level: RoleColors,
	val scanResult: ScanResultColors,
	val action: ActionColors,
)

/** Current theme's colors, provided by `QoNFerenCeRTheme` */
val LocalThemeColors = staticCompositionLocalOf<ThemeColors> { nsBlueThemeColors }

/** Shortcut for current [ThemeColors], provided by `QoNFerenCeRTheme` */
val colors: ThemeColors
	@Composable
	get() = LocalThemeColors.current

////////////// MAIN //////////////
//////////////////////////////////
//////////// SUPPORT /////////////

/** Per-[tr.qonferencer.shared.enums.Role] accent colors */
data class RoleColors(
	val anonym: Color,
	val visitor: Color,
	val volunteer: Color,
	val leader: Color,
	val organiser: Color,
	val admin: Color,
)

/** Per-[tr.qonferencer.shared.enums.MealScanResult] feedback colors */
data class ScanResultColors(
	val approved: Color,
	val alreadyConsumed: Color,
	val noUserFound: Color,
	val notRegisteredPortion: Color,
)

/** Generic action colors, same across every [AppColors] */
data class ActionColors(
	val approve: Color,
	val delete: Color,
	val hyperlinkText: Color,
)

/** Default role colors */
val standardRoleColors = RoleColors(
	anonym = Color(0xFF090CF5),
	visitor = Color(0xFF0003E0),
	volunteer = Color(0xFF6AC706),
	leader = Color(0xFFDEA813),
	organiser = Color(0xFFDE5A13),
	admin = Color(0xFF550055),
)

/** Default scan-result colors */
val standardScanResultColors = ScanResultColors(
	approved = Color(0xFF669900),
	alreadyConsumed = Color(0xFF777216),
	noUserFound = Color(0xFFCC0000),
	notRegisteredPortion = Color(0xFF663300),
)

/** Default action colors */
val standardActionColors = ActionColors(
	approve = Color(0xFF09A148),
	delete = Color(0xFFCC0000),
	hyperlinkText = Color(0xFFF1BC34),
)

//////////// SUPPORT /////////////
//////////////////////////////////
