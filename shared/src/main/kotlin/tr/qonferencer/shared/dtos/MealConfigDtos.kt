package tr.qonferencer.shared.dtos

import java.time.Instant

/**
 * Meal serving window
 * @property id Window id
 * @property nameKey Translation key of window name (breakfast, ...)
 * @property startsAt Serving start
 * @property endsAt Serving end
 */
data class MealWindowDto(
	val id: Long,
	val nameKey: String,
	val startsAt: Instant,
	val endsAt: Instant,
)

/**
 * One meal user is registered for
 * @property windowId Which [MealWindowDto] this entry belongs to
 * @property variantKey Translation key of meal variant
 */
data class UserMealEntryDto(
	val windowId: Long,
	val variantKey: String,
)
