package tr.qonferencer.backend.meal

import tr.qonferencer.shared.dtos.MealWindowDto
import tr.qonferencer.shared.dtos.UserMealEntryDto

fun MealWindow.toDto() = MealWindowDto(id, nameKey, startsAt, endsAt)

fun MealReservation.toUserMealEntry() = UserMealEntryDto(id.windowId, variantKey)
