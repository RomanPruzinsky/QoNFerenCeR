package tr.qonferencer.translations

import androidx.compose.runtime.staticCompositionLocalOf
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.data.local.PrefKey
import tr.qonferencer.data.local.PrefsStorager
import tr.qonferencer.shared.dtos.LanguageDto
import tr.qonferencer.shared.dtos.SplashDto
import tr.qonferencer.shared.dtos.TranslationDto

/** Currently selected language */
val LocalLanguage = staticCompositionLocalOf<String?> { null }

/**
 * Server-keyed translations for current splash
 * @property languages Available languages
 * @property translations Translation entries for all keys/languages
 */
data class TranslationState(
	val languages: List<LanguageDto>,
	val translations: List<TranslationDto>,
) {
	/** Fallback language ([LanguageDto.isDefault]) */
	@get:JsonIgnore
	val defaultLangCode: String? get() = languages.firstOrNull { it.isDefault }?.code

	/**
	 * Resolves [key] for [langCode]: server text -> default-lang text -> raw key
	 * @param key Translation key
	 * @param langCode Language to resolve [key] in
	 */
	fun translate(key: String, langCode: String?): String {
		fun textIn(lang: String?) = translations.firstOrNull { it.key == key && it.langCode == lang }?.text
		return textIn(langCode) ?: textIn(defaultLangCode) ?: key
	}
}

/** Manages active language */
class Language(
	private val prefsStorager: PrefsStorager,
) {
	private val mapper = jacksonObjectMapper()
	private val _options = MutableStateFlow(loadCachedOptions())
	val options: StateFlow<TranslationState> = _options.asStateFlow()
	
	private val _current = MutableStateFlow(prefsStorager.getString(PrefKey.APP_LANGUAGE))
	val current: StateFlow<String?> = _current.asStateFlow()
	
	fun setNewData(splash: SplashDto) {
		val newState = TranslationState(splash.languages, splash.translations)
		_options.value = newState
		prefsStorager.putString(PrefKey.TRANSLATIONS, mapper.writeValueAsString(newState))
		if (_current.value == null) newState.defaultLangCode?.let { select(it) }
	}

	/** Selects [langCode] as [current] */
	fun select(langCode: String) {
		prefsStorager.putString(PrefKey.APP_LANGUAGE, langCode)
		_current.value = langCode
	}
	
	private fun loadCachedOptions(): TranslationState =
		prefsStorager.getString(PrefKey.TRANSLATIONS)?.let { mapper.readValue<TranslationState>(it) }
			?: TranslationState(emptyList(), emptyList())
}
