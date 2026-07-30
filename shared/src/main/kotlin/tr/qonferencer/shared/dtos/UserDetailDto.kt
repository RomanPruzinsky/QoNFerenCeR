package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.Role

/**
 * Full profile of one attendee — self (embedded in splash's `me`) or someone else (`GET /users/{userId}`)
 * @property isSpeaker Orthogonal to [role] — a speaker can sit anywhere on the ladder
 * @property canCheckByName Grants the info-desk search; required on top of an `ORGANISER` [role]
 * @property meals Which window gets which variant
 */
data class UserDetailDto(
	val userId: Long,
	val fullName: String,
	val username: String,
	val role: Role,
	val isSpeaker: Boolean,
	val canCheckByName: Boolean,
	val customData: Map<String, Any?> = emptyMap(),
	val meals: List<UserMealEntryDto> = emptyList(),
)
