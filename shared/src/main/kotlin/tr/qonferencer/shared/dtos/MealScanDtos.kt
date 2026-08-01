package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.ScannerType
import java.util.UUID

/**
 * Meal scan request data
 * @property token User's HMAC token
 * @property mealWindowId Which window is scan for
 * @property idempotencyKey Deduplication for scanned meal when wifi is bad (refresh on bad network with same key)
 * @property scannerType How phone obtained [token]
 */
data class MealScanRequestDto(
	val token: String,
	val mealWindowId: Long,
	val idempotencyKey: UUID,
	val scannerType: ScannerType,
)

/**
 * Result of meal scan
 * @property result Scan outcome
 * @property variantKey Translation key of portion variant
 */
data class MealScanResultDto(
	val result: MealScanResult,
	val variantKey: String?,
)
