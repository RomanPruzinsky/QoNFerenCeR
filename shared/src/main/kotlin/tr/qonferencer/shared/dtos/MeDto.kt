package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.Role

/**
 * Current user's state
 * @property userId Internal user's id, shorter than `kcSub`
 * @property role User's [Role]
 * @property isSpeaker Whether is speaker
 * @property consented Whether approved GDPR things
 * @property qrSecret HMAC secret for generating QRcode every N seconds
 * @property customData Custom data
 * @property meals Meals the user is registered for
 */
data class MeDto(
	val userId: Long,
	val role: Role,
	val isSpeaker: Boolean,
	val consented: Boolean,
	val qrSecret: String,
	val customData: Map<String, Any?> = emptyMap(),
	val meals: List<UserMealEntryDto> = emptyList(),
)
