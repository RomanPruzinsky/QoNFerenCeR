package tr.qonferencer.backend.admin

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.DEFAULT_PAGING_SIZE
import tr.qonferencer.shared.dtos.CreateUserSlotDto
import tr.qonferencer.shared.dtos.SlotCredentialsDto
import tr.qonferencer.shared.dtos.SlotDto
import tr.qonferencer.shared.dtos.UpdateUserSlotDto

/** Admin-only slot provisioning + login re-issue; [SlotService] gates every method */
@RestController
class AdminController(
	private val slotService: SlotService,
) {
	@PostMapping(ApiPaths.Admin.ADD_USER)
	@ResponseStatus(HttpStatus.CREATED)
	fun create(@RequestBody req: CreateUserSlotDto): SlotDto = slotService.createUserSlot(req)

	@GetMapping(ApiPaths.Admin.GET_ALL_USERS)
	fun list(@PageableDefault(size = DEFAULT_PAGING_SIZE) pageable: Pageable): Page<SlotDto> =
		slotService.listSlots(pageable)

	@PostMapping(ApiPaths.Admin.LOGIN)
	fun login(@PathVariable userId: Long): SlotCredentialsDto = slotService.issueLogin(userId)

	@PutMapping(ApiPaths.Admin.UPDATE_USER)
	fun update(@PathVariable userId: Long, @RequestBody req: UpdateUserSlotDto): SlotDto =
		slotService.updateUserSlot(userId, req)

	@PostMapping(ApiPaths.Admin.REVOKE)
	fun revoke(@PathVariable userId: Long): SlotDto = slotService.revokeDevice(userId)

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
