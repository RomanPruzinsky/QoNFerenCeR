package tr.qonferencer.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
	HOME(Role.ANONYM, "nav.home", Icons.Default.Home),
	LOGIN(Role.ANONYM, "nav.login", Icons.Default.QrCode),
	SETTINGS(Role.ANONYM, "nav.settings", Icons.Default.Settings),
	;

	companion object {
		/** Destination shown at launch and as fallback */
		val startDest = HOME
	}
}
