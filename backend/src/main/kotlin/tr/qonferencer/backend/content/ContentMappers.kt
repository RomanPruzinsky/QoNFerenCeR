package tr.qonferencer.backend.content

import tr.qonferencer.shared.dtos.CustomScreenDto
import tr.qonferencer.shared.dtos.LanguageDto
import tr.qonferencer.shared.dtos.TranslationDto

fun Language.toDto() = LanguageDto(code, name, isDefault)

fun Translation.toDto() = TranslationDto(id.key, id.langCode, text)

fun CustomScreen.toDto() = CustomScreenDto(id, titleKey, icon, minRole, isStartingScreen)
