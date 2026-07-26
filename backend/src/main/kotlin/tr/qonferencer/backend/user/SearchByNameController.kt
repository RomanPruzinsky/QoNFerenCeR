package tr.qonferencer.backend.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.SlotDto

@RestController
class SearchByNameController(
	private val searchService: SearchByNameService,
) {
	@GetMapping(ApiPaths.SEARCH_BY_NAME)
	fun search(@RequestParam("q") query: String): List<SlotDto> = searchService.search(query)
}
