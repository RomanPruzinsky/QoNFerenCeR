package tr.qonferencer.backend.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.DEFAULT_PAGING_SIZE
import tr.qonferencer.shared.dtos.UserDisplayDto

@RestController
class SearchByNameController(
	private val searchService: SearchByNameService,
) {
	@GetMapping(ApiPaths.SEARCH_BY_NAME)
	fun search(
		@RequestParam("searchFor") query: String,
		@PageableDefault(size = DEFAULT_PAGING_SIZE) pageable: Pageable,
	): Page<UserDisplayDto> = searchService.search(query, pageable)
}
