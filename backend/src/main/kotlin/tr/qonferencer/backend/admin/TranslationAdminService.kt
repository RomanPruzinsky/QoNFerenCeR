package tr.qonferencer.backend.admin

import org.springframework.stereotype.Service
import tr.qonferencer.backend.common.badRequest
import tr.qonferencer.backend.content.Language
import tr.qonferencer.backend.content.LanguageRepository
import tr.qonferencer.backend.content.Translation
import tr.qonferencer.backend.content.TranslationId
import tr.qonferencer.backend.content.TranslationRepository
import tr.qonferencer.backend.content.toDto
import tr.qonferencer.shared.dtos.AllTranslationsDto
import tr.qonferencer.shared.dtos.LanguageDto
import tr.qonferencer.shared.dtos.TranslationDto

/** Admin CRUD over languages + translations; [set] replaces whole state in one go */
@Service
class TranslationAdminService(
	private val languages: LanguageRepository,
	private val translations: TranslationRepository,
) {
	fun get(): AllTranslationsDto = AllTranslationsDto(
		languages = languages.findAll().map { it.toDto() },
		translations = translations.findAll().map { it.toDto() },
	)

	/** Replaces whole language/translation state with [req] */
	fun set(req: AllTranslationsDto): AllTranslationsDto {
		if (req.languages.none { it.isDefault }) throw badRequest("must keep exactly one default language")

		val incomingLangCodes = req.languages.map { it.code }.toSet()
		val incomingIds = req.translations.map { it.key to it.langCode }.toSet()

		languages.saveAll(req.languages.map { it.toEntity() })
		translations.saveAll(req.translations.map { it.toEntity() })

		translations.findAll()
			.filterNot { (it.id.key to it.id.langCode) in incomingIds }
			.let(translations::deleteAll)

		languages.findAll()
			.filterNot { it.code in incomingLangCodes }
			.let(languages::deleteAll)

		return get()
	}

	private fun LanguageDto.toEntity() = Language(code, name, isDefault)

	private fun TranslationDto.toEntity() = Translation(TranslationId(key, langCode), text)
}
