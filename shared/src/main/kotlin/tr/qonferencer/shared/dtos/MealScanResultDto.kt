package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.MealScanResult

/**
 * Result of meal scan
 * @property result Scan outcome
 * @property userId User being scanned
 * @property variantKey Translation key of portion variant
 */
data class MealScanResultDto(
	val result: MealScanResult,
	val userId: Long?,
	val variantKey: String?,
)
