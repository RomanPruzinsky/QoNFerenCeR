package tr.qonferencer.backend.user

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.DEFAULT_PAGING_SIZE
import tr.qonferencer.shared.dtos.MealSecretDto
import tr.qonferencer.shared.dtos.MealSecretRequestDto
import tr.qonferencer.shared.dtos.PageDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.dtos.UserDisplayDto

@RestController
class UserController(
	private val userDetailService: UserDetailService,
	private val mealSecretService: MealSecretService,
	private val searchService: SearchByNameService,
) {
	@GetMapping(ApiPaths.User.BY_ID)
	fun detail(@PathVariable userId: Long): UserDetailDto = userDetailService.detail(userId)

	@GetMapping(ApiPaths.User.BY_NAME)
	fun search(
		@RequestParam("searchFor") query: String,
		@PageableDefault(size = DEFAULT_PAGING_SIZE) pageable: Pageable,
	): PageDto<UserDisplayDto> = searchService.search(query, pageable)

	@PostMapping(ApiPaths.User.MEAL_SECRET)
	fun mealSecret(@RequestBody request: MealSecretRequestDto): MealSecretDto =
		MealSecretDto(mealSecretService.reveal(request.password))
}
