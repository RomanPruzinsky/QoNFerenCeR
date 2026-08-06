package tr.qonferencer.backend.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.UserDetailDto

@RestController
class UserController(
	private val userDetailService: UserDetailService,
) {
	@GetMapping(ApiPaths.User.BY_ID)
	fun detail(@PathVariable userId: Long): UserDetailDto = userDetailService.detail(userId)
}
