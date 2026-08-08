package tr.qonferencer.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.data.local.PrefKey
import tr.qonferencer.data.local.PrefsStorager
import tr.qonferencer.theme.color.AppColors

/** Manages user's selected [AppColors] */
class ColorPrefs(
	private val prefsStorager: PrefsStorager,
) {
	private val _currentColors = MutableStateFlow(loadColors())
	val currentColors: StateFlow<AppColors> = _currentColors.asStateFlow()

	/** Selects [appColors] as current */
	fun setColors(appColors: AppColors) {
		prefsStorager.putString(PrefKey.APP_THEME, appColors.name)
		_currentColors.value = appColors
	}
	
	private fun loadColors(): AppColors = prefsStorager.getString(PrefKey.APP_THEME)
		?.let { name -> AppColors.entries.firstOrNull { it.name == name } }
		?: AppColors.NS_BLUE
}
