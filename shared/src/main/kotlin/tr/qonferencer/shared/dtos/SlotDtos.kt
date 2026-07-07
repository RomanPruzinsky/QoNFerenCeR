package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.Role

/** Create [count] slots for users with specific [role] */
data class CreateSlotsDto(
	val count: Int,
	val role: Role = Role.VISITOR,
)

/** Obtain credentials for slot */
data class SlotCredentialsDto(
	val username: String,
	val password: String,
)
