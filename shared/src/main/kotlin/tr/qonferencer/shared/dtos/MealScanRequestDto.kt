package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.CustomDataType
import tr.qonferencer.shared.enums.ScannerType
import java.util.UUID

/**
 * Meal scan request data
 * @property token User's HMAC token
 * @property mealWindowId Which window is scan for
 * @property idempotencyKey Deduplication for scanned meal when wifi is bad (refresh on bad network with same key)
 * @property scannerType How phone obtained [token]
 * @property customData Custom data
 */
data class MealScanRequestDto(
	val token: String,
	val mealWindowId: Long,
	val idempotencyKey: UUID,
	val scannerType: ScannerType,
	val customData: CustomDataType? = null,
)
