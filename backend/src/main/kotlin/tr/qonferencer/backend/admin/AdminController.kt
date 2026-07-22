package tr.qonferencer.backend.admin

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.CreateSlotDto
import tr.qonferencer.shared.dtos.SlotCredentialsDto
import tr.qonferencer.shared.dtos.SlotDto

/** Admin-only slot provisioning + login re-issue; [SlotService] gates every method */
@RestController
class AdminController(
	private val slotService: SlotService,
) {
	@PostMapping(ApiPaths.Admin.SLOTS)
	@ResponseStatus(HttpStatus.CREATED)
	fun create(@RequestBody req: CreateSlotDto): SlotDto = slotService.createSlot(req)

	@GetMapping(ApiPaths.Admin.SLOTS)
	fun list(): List<SlotDto> = slotService.listSlots()

	@PostMapping(ApiPaths.Admin.SLOT_LOGIN)
	fun login(@PathVariable userId: Long): SlotCredentialsDto = slotService.issueLogin(userId)
}
