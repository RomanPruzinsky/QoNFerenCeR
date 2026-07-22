package tr.qonferencer.backend.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.SlotDto

@RestController
class AttendeeLookupController(
	private val lookupService: AttendeeLookupService,
) {
	@GetMapping(ApiPaths.ATTENDEES)
	fun search(@RequestParam("q") query: String): List<SlotDto> = lookupService.search(query)
}
