package tr.qonferencer.backend.admin

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.CreateUserSlotDto
import tr.qonferencer.shared.dtos.SlotCredentialsDto
import tr.qonferencer.shared.dtos.SlotDto
import tr.qonferencer.shared.dtos.UpdateUserSlotDto

/** Admin-only slot provisioning + login re-issue; [SlotService] gates every method */
@RestController
class AdminController(
	private val slotService: SlotService,
) {
	@PostMapping(ApiPaths.Admin.SLOTS)
	@ResponseStatus(HttpStatus.CREATED)
	fun create(@RequestBody req: CreateUserSlotDto): SlotDto = slotService.createUserSlot(req)

	@GetMapping(ApiPaths.Admin.SLOTS)
	fun list(): List<SlotDto> = slotService.listSlots()

	@PostMapping(ApiPaths.Admin.SLOT_LOGIN)
	fun login(@PathVariable userId: Long): SlotCredentialsDto = slotService.issueLogin(userId)

	@PutMapping(ApiPaths.Admin.SLOT_BY_ID)
	fun update(@PathVariable userId: Long, @RequestBody req: UpdateUserSlotDto): SlotDto =
		slotService.updateUserSlot(userId, req)

	@PostMapping(ApiPaths.Admin.SLOT_REVOKE)
	fun revoke(@PathVariable userId: Long): SlotDto = slotService.revokeDevice(userId)

	/** Erasure on request: the attendee asks at the desk, the admin does it (GDPR art. 17) */
	@DeleteMapping(ApiPaths.Admin.SLOT_BY_ID)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun delete(@PathVariable userId: Long) = slotService.deleteUserSlot(userId)
}
