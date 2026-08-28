package tr.qonferencer.navigation

import androidx.compose.runtime.Composable
import tr.qonferencer.navigation.QoNFerenCeRDestinations.ABOUT_APP
import tr.qonferencer.navigation.QoNFerenCeRDestinations.HOME
import tr.qonferencer.navigation.QoNFerenCeRDestinations.LOGIN
import tr.qonferencer.navigation.QoNFerenCeRDestinations.MY_PROFILE
import tr.qonferencer.navigation.QoNFerenCeRDestinations.SETTINGS
import tr.qonferencer.navigation.QoNFerenCeRDestinations.USER_CHECK
import tr.qonferencer.screens.aboutapp.AboutAppScreen
import tr.qonferencer.screens.home.HomeScreen
import tr.qonferencer.screens.login.LoginScreen
import tr.qonferencer.screens.myprofile.MyProfileScreen
import tr.qonferencer.screens.settings.SettingsScreen
import tr.qonferencer.screens.usercheck.detail.UserCheckScreen

/** Directs each [QoNFerenCeRDestinations] entry to its composable */
@Composable
fun QoNFerenCeRDestinations.ProcessScreen(): Unit = when (this) {
	HOME -> HomeScreen()
	LOGIN -> LoginScreen()
	SETTINGS -> SettingsScreen()
	MY_PROFILE -> MyProfileScreen()
	ABOUT_APP -> AboutAppScreen()
	USER_CHECK -> UserCheckScreen()
}
