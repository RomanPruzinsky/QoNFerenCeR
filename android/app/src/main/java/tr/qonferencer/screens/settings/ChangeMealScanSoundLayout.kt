package tr.qonferencer.screens.settings

import androidx.compose.runtime.Composable
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.BasicSwitch
import tr.qonferencer.trons.states.collectValue

@Composable
fun ChangeMealScanSoundLayout() {
	SettingsCardLayout(introduction = dynamicTranslation("settings.mealScanSound")) {
		val soundEnabled = QoNFerenCeRApp.mealScanAudioPrefs.soundEnabled.collectValue()

		BasicSwitch(
			checked = soundEnabled,
			action = { enabled -> QoNFerenCeRApp.mealScanAudioPrefs.setSoundEnabled(enabled) },
		)
	}
}
