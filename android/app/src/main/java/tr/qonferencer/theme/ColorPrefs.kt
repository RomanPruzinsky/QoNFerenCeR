package tr.qonferencer.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.data.local.PrefKey
import tr.qonferencer.data.local.PrefsStorager

/** Manages user's selected [AppColorsOptions] */
class ColorPrefs(
	private val prefsStorager: PrefsStorager,
) {
	private val _currentColors = MutableStateFlow(loadColors())
	val currentColors: StateFlow<AppColorsOptions> = _currentColors.asStateFlow()

	/** Selects [appColors] as current */
	fun setColors(appColors: AppColorsOptions) {
		prefsStorager.putString(PrefKey.APP_COLORS, appColors.name)
		_currentColors.value = appColors
	}
	
	private fun loadColors(): AppColorsOptions = prefsStorager.getString(PrefKey.APP_COLORS)
		?.let { name -> AppColorsOptions.entries.firstOrNull { it.name == name } }
		?: AppColorsOptions.NS_BLUE
}
