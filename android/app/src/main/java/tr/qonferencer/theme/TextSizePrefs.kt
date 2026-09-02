package tr.qonferencer.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.data.local.PrefKey
import tr.qonferencer.data.local.PrefsStorager

/** Manages user's selected font size enlargement */
class TextSizePrefs(
	private val prefsStorager: PrefsStorager,
) {
	private val _currentEnlargement = MutableStateFlow(loadEnlargement())
	val currentEnlargement: StateFlow<Int> = _currentEnlargement.asStateFlow()

	/** Selects [enlargement] as current, trimmed to [FontSizeEnlarger.MIN]..[FontSizeEnlarger.MAX] */
	fun setEnlargement(enlargement: Int) {
		val safeValue = enlargement.coerceIn(FontSizeEnlarger.MIN, FontSizeEnlarger.MAX)
		prefsStorager.putInt(PrefKey.APP_FONT_SIZE, safeValue)
		_currentEnlargement.value = safeValue
	}

	private fun loadEnlargement(): Int = prefsStorager.getInt(PrefKey.APP_FONT_SIZE) ?: 0
}
