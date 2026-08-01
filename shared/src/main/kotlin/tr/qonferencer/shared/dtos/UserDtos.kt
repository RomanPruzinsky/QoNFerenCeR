package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.CustomDataType
import tr.qonferencer.shared.enums.Role

/** Full profile of one user */
data class UserDetailDto(
	val userId: Long,
	val fullName: String,
	val role: Role,
	val isSpeaker: Boolean,
	val canCheckByName: Boolean,
	val meals: List<UserMealEntryDto> = emptyList(),
	val customData: CustomDataType = emptyMap(),
)

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

/** Similar to [UserDetailDto], but without id, as this is for modifying */
data class ModifyableUserDataDto(
	val fullName: String,
	val role: Role = Role.VISITOR,
	val isSpeaker: Boolean = false,
	val canCheckByName: Boolean = false,
	val meals: List<UserMealEntryDto> = emptyList(),
	val customData: CustomDataType = emptyMap(),
)

/** Data required for login */
data class LoginCredentialsDto(
	val username: String,
	val password: String,
	val qrSecret: String,
)
