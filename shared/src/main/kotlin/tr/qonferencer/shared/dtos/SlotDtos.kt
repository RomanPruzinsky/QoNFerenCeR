package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.Role

/**
 * Data for slot
 * @property isSpeaker Orthogonal to [role] — a speaker can sit anywhere on the ladder
 * @property canCheckByName Grants the info-desk lookup; required on top of an `ORGANISER` [role]
 */
data class CreateSlotDto(
	val fullName: String,
	val role: Role = Role.VISITOR,
	val isSpeaker: Boolean = false,
	val canCheckByName: Boolean = false,
	val customData: Map<String, Any?> = emptyMap(),
)

/** Provisioned slot (Keycloak user + app anchor) */
data class SlotDto(
	val userId: Long,
	val fullName: String,
	val username: String? = null,
	val customData: Map<String, Any?> = emptyMap(),
)

/** Login credentials for slot */
data class SlotCredentialsDto(
	val username: String,
	val password: String,
)
