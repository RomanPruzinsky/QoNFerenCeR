package tr.qonferencer.backend.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.MeDto

@RestController
class MeController(
	private val meService: MeService,
) {
	@GetMapping(ApiPaths.Me.ROOT)
	fun me(): MeDto = meService.me()
}
