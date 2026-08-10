package tr.qonferencer

import android.app.Application
import android.content.Context
import tr.qonferencer.data.local.PrefsStorager
import tr.qonferencer.theme.ThemePrefs
import tr.qonferencer.translations.Language

class QoNFerenCeRApp : Application() {
	companion object {
		lateinit var appContext: Context
		lateinit var tokenStore: PrefsStorager
		lateinit var themePrefs: ThemePrefs
		lateinit var language: Language
	}

	override fun onCreate() {
		super.onCreate()
		appContext = applicationContext
		tokenStore = PrefsStorager(this)
		themePrefs = ThemePrefs(tokenStore)
		language = Language(tokenStore)
	}
}
