package tr.qonferencer.backend.content

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.SplashDto

@RestController
class SplashController(
	private val splashService: SplashService,
) {
	@GetMapping(ApiPaths.SPLASH)
	fun splash(): SplashDto = splashService.build()
}
