package tr.qonferencer.shared.dtos

/**
 * Available language
 * @property code Language code ("en","sk",...)
 * @property name Readable name ("English","Slovenčina",...)
 * @property isDefault Whether is picked on first launch + fallback
 */
data class LanguageDto(
	val code: String,
	val name: String,
	val isDefault: Boolean = false,
)

/**
 * Translation entry for [key] per [langCode]
 * @property key Key to match translation
 * @property langCode **Language** code in which is [text] ("en","sk",...)
 * @property text Text to display for [key]+[langCode]
 */
data class TranslationDto(
	val key: String,
	val langCode: String,
	val text: String,
)

/**
 * Whole translation state: splash reads it, admin edits it
 * @property languages All configured languages
 * @property translations All key+language translation entries
 */
data class AllTranslationsDto(
	val languages: List<LanguageDto>,
	val translations: List<TranslationDto>,
)
