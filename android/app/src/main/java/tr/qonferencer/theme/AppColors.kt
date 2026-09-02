package tr.qonferencer.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.MealScanResult.ALREADY_CONSUMED
import tr.qonferencer.shared.enums.MealScanResult.APPROVED
import tr.qonferencer.shared.enums.MealScanResult.NOT_REGISTERED_PORTION
import tr.qonferencer.shared.enums.MealScanResult.NO_USER_FOUND
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.shared.enums.Role.ADMIN
import tr.qonferencer.shared.enums.Role.ANONYM
import tr.qonferencer.shared.enums.Role.LEADER
import tr.qonferencer.shared.enums.Role.ORGANISER
import tr.qonferencer.shared.enums.Role.VISITOR
import tr.qonferencer.shared.enums.Role.VOLUNTEER
import tr.qonferencer.theme.appColorsOptions.auroraAppColors
import tr.qonferencer.theme.appColorsOptions.blackPinkAppColors
import tr.qonferencer.theme.appColorsOptions.deepOrangeAppColors
import tr.qonferencer.theme.appColorsOptions.deepSeaAppColors
import tr.qonferencer.theme.appColorsOptions.forestGreenAppColors
import tr.qonferencer.theme.appColorsOptions.midnightBlueAppColors
import tr.qonferencer.theme.appColorsOptions.mochaCoffeeAppColors
import tr.qonferencer.theme.appColorsOptions.nsBlueAppColors
import tr.qonferencer.theme.appColorsOptions.sakuraNightAppColors
import tr.qonferencer.theme.appColorsOptions.slateSteelAppColors
import tr.qonferencer.theme.appColorsOptions.solarizedAppColors
import tr.qonferencer.theme.appColorsOptions.trAppColors

//////////////////////////////////
////////////// MAIN //////////////

/** Selectable app palette, persisted by `ThemePrefs` */
enum class AppColorsOptions(
	val colors: AppColors,
) {
	NS_BLUE(nsBlueAppColors),
	MIDNIGHT_BLUE(midnightBlueAppColors),
	SLATE_STEEL(slateSteelAppColors),
	SAKURA_NIGHT(sakuraNightAppColors),
	
	AURORA(auroraAppColors),
	MOCHA_COFFEE(mochaCoffeeAppColors),
	DEEP_SEA(deepSeaAppColors),
	
	BLACK_PINK(blackPinkAppColors),
	DEEP_ORANGE(deepOrangeAppColors),
	TR(trAppColors),
	SOLARIZED(solarizedAppColors),
	FOREST_GREEN(forestGreenAppColors),
}

/** Full color palette of one [AppColorsOptions] */
data class AppColors(
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
val LocalAppColors = staticCompositionLocalOf { nsBlueAppColors }

/** Shortcut for current [AppColors], provided by `QoNFerenCeRTheme` */
val colors: AppColors
	@Composable
	get() = LocalAppColors.current

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
	val adminLight: Color,
)

/** Per [Role] coolor */
val Role.color: Color
	@Composable get() = when (this) {
		ANONYM -> colors.level.anonym
		VISITOR -> colors.level.visitor
		VOLUNTEER -> colors.level.volunteer
		LEADER -> colors.level.leader
		ORGANISER -> colors.level.organiser
		ADMIN -> colors.level.admin
	}

/** Per [MealScanResult] color */
val MealScanResult.color: Color
	@Composable get() = when (this) {
		APPROVED -> colors.scanResult.approved
		ALREADY_CONSUMED -> colors.scanResult.alreadyConsumed
		NO_USER_FOUND -> colors.scanResult.noUserFound
		NOT_REGISTERED_PORTION -> colors.scanResult.notRegisteredPortion
	}

/** Per-[tr.qonferencer.shared.enums.MealScanResult] feedback colors */
data class ScanResultColors(
	val approved: Color,
	val alreadyConsumed: Color,
	val noUserFound: Color,
	val notRegisteredPortion: Color,
)

/** Generic action colors, same across every [AppColorsOptions] */
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
	adminLight = Color(0xFF9609C9),
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
