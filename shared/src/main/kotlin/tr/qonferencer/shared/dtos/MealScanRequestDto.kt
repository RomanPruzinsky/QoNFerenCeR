package tr.qonferencer.shared.dtos

/**
 * Meal scan request data
 * @property token user's HMAC token
 * @property mealWindowId Which window is scan for
 * @property idempotencyKey Deduplication for scanned meal when wifi is bad (refresh on bad network with same key)
 * @property customJson Custom data, keys in [CustomElementDef]
 */
data class MealScanRequestDto(
	val token: String,
	val mealWindowId: Long,
	val idempotencyKey: String,
	val customJson: Map<String, Any?>? = null,
)
