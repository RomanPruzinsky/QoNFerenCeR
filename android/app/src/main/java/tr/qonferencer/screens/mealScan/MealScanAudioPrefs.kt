package tr.qonferencer.screens.mealScan

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.data.local.PrefKey
import tr.qonferencer.data.local.PrefsStorager

/** Manages whether meal scan plays sound */
class MealScanAudioPrefs(
	private val prefsStorager: PrefsStorager,
) {
	private val _soundEnabled = MutableStateFlow(loadSoundEnabled())
	val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

	/** Selects whether feedback sounds play */
	fun setSoundEnabled(enabled: Boolean) {
		prefsStorager.putBoolean(PrefKey.MEAL_SCAN_SOUND, enabled)
		_soundEnabled.value = enabled
	}
	
	private fun loadSoundEnabled(): Boolean = prefsStorager.getBoolean(PrefKey.MEAL_SCAN_SOUND) ?: true
}
