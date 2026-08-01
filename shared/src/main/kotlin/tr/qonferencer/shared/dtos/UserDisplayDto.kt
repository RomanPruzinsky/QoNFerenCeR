package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.Role

/**
 * Simple data of user searched by name
 * @see [UserDetailDto]
 */
data class UserDisplayDto(
	val userId: Long,
	val fullName: String,
	val role: Role,
	val isSpeaker: Boolean,
)
