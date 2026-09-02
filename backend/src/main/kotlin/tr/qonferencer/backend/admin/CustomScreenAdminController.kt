package tr.qonferencer.backend.admin

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.CustomScreenAdminDto

@RestController
class CustomScreenAdminController(
	private val screens: CustomScreenAdminService,
) {
	@GetMapping(ApiPaths.Admin.CustomScreens.ROOT)
	fun list(): List<CustomScreenAdminDto> = screens.list()

	@PutMapping(ApiPaths.Admin.CustomScreens.BY_ID)
	fun update(
		@PathVariable id: String,
		@RequestBody req: CustomScreenAdminDto,
	): CustomScreenAdminDto = screens.upsert(id, req)

	@DeleteMapping(ApiPaths.Admin.CustomScreens.BY_ID)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun delete(@PathVariable id: String) = screens.delete(id)
}
