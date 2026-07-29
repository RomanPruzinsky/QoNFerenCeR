package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.Role

/** One row of an info-desk name search result; click through to `GET /users/{userId}` for the rest */
data class SearchByNameDisplayDto(
	val userId: Long,
	val fullName: String,
	val role: Role,
	val isSpeaker: Boolean,
)
