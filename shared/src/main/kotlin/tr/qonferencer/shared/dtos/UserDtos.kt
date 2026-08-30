package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.CustomDataType
import tr.qonferencer.shared.enums.Role

/** Full profile of one user */
data class UserDetailDto(
	val userId: Long,
	val fullName: String,
	val role: Role,
	val isSpeaker: Boolean,
	val canCheckUsers: Boolean,
	val canFoodCheck: Boolean,
	val meals: List<UserMealEntryDto> = emptyList(),
	val customData: CustomDataType = emptyMap(),
) {
	companion object {
		/** @return [user]'s [role], or [Role.ANONYM] when [user] is `null` (nobody logged in) */
		fun roleOrAnonym(user: UserDetailDto?): Role = user?.role ?: Role.ANONYM
	}
}

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
	val canCheckUsers: Boolean = false,
	val canFoodCheck: Boolean = false,
	val meals: List<UserMealEntryDto> = emptyList(),
	val customData: CustomDataType = emptyMap(),
)

/** Data required for login */
data class LoginCredentialsDto(
	val username: String,
	val password: String,
)

/** User's data with credentials */
data class SlotProvisionedDto(
	val user: UserDetailDto,
	val credentials: LoginCredentialsDto,
)

/** Re-proves caller's own password to release [MealSecretDto] */
data class MealSecretRequestDto(
	val password: String,
)

data class MealSecretDto(
	val mealSecret: String,
)
