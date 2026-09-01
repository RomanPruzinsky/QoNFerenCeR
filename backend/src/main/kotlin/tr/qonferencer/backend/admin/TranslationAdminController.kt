package tr.qonferencer.backend.admin

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.AllTranslationsDto

@RestController
class TranslationAdminController(
	private val service: TranslationAdminService,
) {
	@GetMapping(ApiPaths.Admin.Translations.ROOT)
	fun get(): AllTranslationsDto = service.get()

	@PutMapping(ApiPaths.Admin.Translations.ROOT)
	fun set(@RequestBody req: AllTranslationsDto): AllTranslationsDto = service.set(req)
}
