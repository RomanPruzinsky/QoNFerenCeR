package tr.qonferencer

import android.app.Application
import tr.qonferencer.data.local.PrefsStorager

class QoNFerenCeRApp : Application() {
	companion object {
		lateinit var tokenStore: PrefsStorager
	}

	override fun onCreate() {
		super.onCreate()
		tokenStore = PrefsStorager(this)
	}
}
