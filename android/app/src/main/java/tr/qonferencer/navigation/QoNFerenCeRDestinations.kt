package tr.qonferencer.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import tr.qonferencer.shared.enums.Role

/**
 * Available destinations for navigation, in drawer display order
 * @param minRole Minimal role required to access
 * @param titleKey Translation key for screen's title
 * @param icon Icon shown in navigation drawer
 */
enum class QoNFerenCeRDestinations(
	val minRole: Role,
	val titleKey: String,
	val icon: ImageVector,
) {
	MY_PROFILE(Role.VISITOR, "destination.myProfile", Icons.Default.Person),
	HOME(Role.ANONYM, "destination.home", Icons.Default.Home),
	ABOUT_APP(Role.ANONYM, "destination.aboutApp", Icons.Default.Info),
	USER_CHECK(Role.ORGANISER, "destination.userCheck", Icons.Default.Badge),
	MEAL_SCAN(Role.VOLUNTEER, "destination.mealScan", Icons.Default.Restaurant),
	CREATE_SLOT(Role.ADMIN, "destination.createSlot", Icons.Default.PersonAdd),
	SETTINGS(Role.ANONYM, "destination.settings", Icons.Default.Settings),
	LOGIN(Role.ANONYM, "destination.login", Icons.Default.QrCode),
	;

	companion object {
		/** Destination shown at launch and as fallback */
		val startDest = HOME
	}
}
