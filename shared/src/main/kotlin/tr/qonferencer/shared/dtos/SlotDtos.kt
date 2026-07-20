package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.Role

/** Data for slot */
data class CreateSlotDto(
	val role: Role = Role.VISITOR,
	val customData: Map<String, Any?> = emptyMap(),
)

/** Provisioned slot (Keycloak user + app anchor) */
data class SlotDto(
	val userId: Long,
	val username: String? = null,
	val customData: Map<String, Any?> = emptyMap(),
)

/** Login credentials for slot */
data class SlotCredentialsDto(
	val username: String,
	val password: String,
)
