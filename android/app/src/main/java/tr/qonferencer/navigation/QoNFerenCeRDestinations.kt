package tr.qonferencer.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
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
	HOME(Role.ANONYM, "destination.home", Icons.Default.Home),
	LOGIN(Role.ANONYM, "destination.login", Icons.Default.QrCode),
	SETTINGS(Role.ANONYM, "destination.settings", Icons.Default.Settings),
	MY_PROFILE(Role.VISITOR, "destination.myProfile", Icons.Default.Person),
	ABOUT_APP(Role.ANONYM, "destination.aboutApp", Icons.Default.Info),
	;

	companion object {
		/** Destination shown at launch and as fallback */
		val startDest = HOME
	}
}
