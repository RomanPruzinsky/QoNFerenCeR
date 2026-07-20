package tr.qonferencer.backend.content

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.CustomElement

@RestController
class CustomScreenController(
	private val screenService: CustomScreenService,
) {
	@GetMapping(ApiPaths.CustomScreens.BY_ID)
	fun body(@PathVariable id: String): List<CustomElement> = screenService.body(id)
}
