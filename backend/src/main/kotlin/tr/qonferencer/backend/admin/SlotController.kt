package tr.qonferencer.backend.admin

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.LoginCredentialsDto
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.SlotProvisionedDto
import tr.qonferencer.shared.dtos.UserDetailDto

/** Admin-only slot provisioning + login re-issue; [SlotService] gates every method */
@RestController
class AdminController(
	private val slotService: SlotService,
) {
	@PostMapping(ApiPaths.Admin.ADD_USER)
	@ResponseStatus(HttpStatus.CREATED)
	fun create(@RequestBody req: ModifyableUserDataDto): SlotProvisionedDto = slotService.createUserSlot(req)
	
	@PostMapping(ApiPaths.Admin.LOGIN)
	fun login(@PathVariable userId: Long): LoginCredentialsDto = slotService.issueLogin(userId)
	
	@PutMapping(ApiPaths.Admin.UPDATE_USER)
	fun update(@PathVariable userId: Long, @RequestBody req: ModifyableUserDataDto): UserDetailDto =
		slotService.updateUserSlot(userId, req)
	
	@PostMapping(ApiPaths.Admin.REVOKE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun revoke(@PathVariable userId: Long) = slotService.revokeDevice(userId)

	/**
	 * Erasure on request: the attendee asks at the desk, the admin does it (GDPR art. 17).
	 * 200 = app data and Keycloak user both gone; 207 = app data gone, Keycloak user survives.
	 */
	@DeleteMapping(ApiPaths.Admin.DELETE_USER)
	fun delete(@PathVariable userId: Long): ResponseEntity<Void> = when (slotService.deleteUserSlot(userId)) {
		DeleteOutcome.FULL -> ResponseEntity.status(HttpStatus.OK).build()
		DeleteOutcome.KEYCLOAK_SURVIVED -> ResponseEntity.status(HttpStatus.MULTI_STATUS).build()
	}
}
