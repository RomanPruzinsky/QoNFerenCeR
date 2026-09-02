package tr.qonferencer.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.data.local.PrefKey
import tr.qonferencer.data.local.PrefsStorager

/** Manages user's selected [AvailableFonts] */
class FontPrefs(
	private val prefsStorager: PrefsStorager,
) {
	private val _currentFont = MutableStateFlow(loadFont())
	val currentFont: StateFlow<AvailableFonts> = _currentFont.asStateFlow()

	/** Selects [font] as current */
	fun setFont(font: AvailableFonts) {
		prefsStorager.putInt(PrefKey.APP_FONT_FAMILY, font.ordinal)
		_currentFont.value = font
	}
	
	private fun loadFont(): AvailableFonts = prefsStorager.getInt(PrefKey.APP_FONT_FAMILY)
		?.let { index -> AvailableFonts.entries.getOrNull(index) }
		?: DEFAULT_FONT
}
