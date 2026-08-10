package tr.qonferencer

import android.app.Application
import android.content.Context
import tr.qonferencer.data.local.PrefsStorager
import tr.qonferencer.theme.ThemePrefs
import tr.qonferencer.translations.Language
import tr.qonferencer.user.CurrentUser
import tr.qonferencer.user.CustomScreens

class QoNFerenCeRApp : Application() {
	companion object {
		lateinit var appContext: Context
		lateinit var tokenStore: PrefsStorager
		lateinit var themePrefs: ThemePrefs
		lateinit var language: Language
		lateinit var currentUser: CurrentUser
		lateinit var customScreens: CustomScreens
	}

	override fun onCreate() {
		super.onCreate()
		appContext = applicationContext
		tokenStore = PrefsStorager(this)
		themePrefs = ThemePrefs(tokenStore)
		language = Language(tokenStore)
		currentUser = CurrentUser()
		customScreens = CustomScreens()
	}
}
