package tr.qonferencer

import android.app.Application
import tr.qonferencer.data.local.PrefsStorager
import tr.qonferencer.theme.ThemePrefs

class QoNFerenCeRApp : Application() {
	companion object {
		lateinit var tokenStore: PrefsStorager
		lateinit var themePrefs: ThemePrefs
	}

	override fun onCreate() {
		super.onCreate()
		tokenStore = PrefsStorager(this)
		themePrefs = ThemePrefs(tokenStore)
	}
}
