package tr.qonferencer.shared.dtos

import java.time.Instant

/**
 * Meal serving window (canonical, shared across users)
 * @property id Window id
 * @property nameKey Translation key of the window name
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
 * One meal the current user is registered for
 * @property windowId Which [MealWindowDto] this entry belongs to
 * @property variantKey Translation key of the meal variant the user gets
 */
data class UserMealEntryDto(
	val windowId: Long,
	val variantKey: String,
)
