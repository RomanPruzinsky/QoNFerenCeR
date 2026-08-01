package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.CustomDataType
import tr.qonferencer.shared.enums.Role

/**
 * Full profile of one user
 * @see [UserDisplayDto]
 */
data class UserDetailDto(
	val userId: Long,
	val fullName: String,
	val role: Role,
	val isSpeaker: Boolean,
	val canCheckByName: Boolean,
	val meals: List<UserMealEntryDto> = emptyList(),
	val customData: CustomDataType = emptyMap(),
)
