package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.Role

/**
 * Everything known about one attendee at registration time
 * @property isSpeaker Orthogonal to [role] — a speaker can sit anywhere on the ladder
 * @property canCheckByName Grants the info-desk search; required on top of an `ORGANISER` [role]
 * @property meals Which window gets which variant; an unknown window is refused
 */
data class CreateUserSlotDto(
	val fullName: String,
	val role: Role = Role.VISITOR,
	val isSpeaker: Boolean = false,
	val canCheckByName: Boolean = false,
	val customData: Map<String, Any?> = emptyMap(),
	val meals: List<UserMealEntryDto> = emptyList(),
)

/**
 * Replaces everything mutable about an attendee; a full replacement, not a patch
 * @property meals Replaces the whole reservation list; meals already consumed are left alone
 */
data class UpdateUserSlotDto(
	val fullName: String,
	val role: Role = Role.VISITOR,
	val isSpeaker: Boolean = false,
	val canCheckByName: Boolean = false,
	val customData: Map<String, Any?> = emptyMap(),
	val meals: List<UserMealEntryDto> = emptyList(),
)

/** Provisioned slot (Keycloak user + app anchor) */
data class SlotDto(
	val userId: Long,
	val fullName: String,
	val username: String? = null,
	val customData: Map<String, Any?> = emptyMap(),
)

/**
 * Login credentials for slot
 * @property qrSecret HMAC seed for the attendee's rotating scan token, handed off alongside the QR login
 */
data class SlotCredentialsDto(
	val username: String,
	val password: String,
	val qrSecret: String,
)
