package tr.qonferencer.translations

import androidx.compose.runtime.Composable
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.trons.states.collectValue

/**
 * Resolves [key] to text in current language
 * @param key Translation key
 */
@Composable
fun dynamicTranslation(key: String): String =
	QoNFerenCeRApp.language.options.collectValue().translate(key, LocalLanguage.current)

/**
 * Resolves [key] to text in current language
 * @param key Translation key
 * @see dynamicTranslation
 */
fun rawDynamicTranslation(key: String): String =
	QoNFerenCeRApp.language.options.value.translate(key, QoNFerenCeRApp.language.current.value)
