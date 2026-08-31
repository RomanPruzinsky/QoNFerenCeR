package tr.qonferencer.screens.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import tr.qonferencer.shared.dtos.MealWindowDto
import tr.qonferencer.shared.dtos.TranslationDto
import tr.qonferencer.shared.dtos.UserMealEntryDto
import tr.qonferencer.theme.colors
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CustomDropdownMenu
import tr.qonferencer.trons.defaultLayouts.DefaultHeightSpacer
import tr.qonferencer.trons.defaultLayouts.ProfileEditRow
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.ops.relistC

private const val MEAL_VARIANT_PREFIX = "meal.variant."

/** @return distinct meal variant keys found in [this], e.g. "meal.variant.standard" */
fun List<TranslationDto>.mealVariantKeys(): List<String> =
	this.relist { it.key }.distinct().filter { it.startsWith(MEAL_VARIANT_PREFIX) }.sorted()

/**
 * One editable meal-window row, kept as stable state for [CustomDropdownMenu]
 * @property window Window this row edits
 * @property variantKeys Selectable variants, offered to every window
 */
class MealFieldState(
	val window: MealWindowDto,
	val variantKeys: List<String>,
	existingVariantKey: String? = null,
) {
	/** `0` = not registered, else `variantKeys[selectedIndex - 1]` */
	val selectedIndex = mutableIntStateOf(variantKeys.indexOf(existingVariantKey) + 1)
	val expanded = mutableStateOf(false)
}

/** @return [MealFieldState] rows for [this] windows, seeded from [existing] reservations, offering [variantKeys] */
fun List<MealWindowDto>.toMealFieldStates(
	existing: List<UserMealEntryDto>,
	variantKeys: List<String>,
): List<MealFieldState> = this.relist { window ->
	MealFieldState(window, variantKeys, existing.firstOrNull { it.windowId == window.id }?.variantKey)
}

/** @return [UserMealEntryDto] list built from [this], dropping windows left at "not registered" */
fun List<MealFieldState>.toMeals(): List<UserMealEntryDto> = mapNotNull { field ->
	val index = field.selectedIndex.intValue
	if (index == 0) null
	else UserMealEntryDto(field.window.id, field.variantKeys[index - 1])
}

/**
 * Editable per-window meal reservation rows
 * @param fields Rows to edit, mutated in place by dropdown selection
 */
@Composable
fun MealEditor(fields: List<MealFieldState>) {
	fields.forEach { field ->
		DefaultHeightSpacer()
		ProfileEditRow(dynamicTranslation(field.window.nameKey)) {
			CustomDropdownMenu(
				options = listOf(dynamicTranslation("admin.meal.notRegistered")) +
					field.variantKeys.relistC { dynamicTranslation(it) },
				selected = field.selectedIndex,
				expanded = field.expanded,
				arrowAtStart = false,
				selectedColor = colors.clickable,
			)
		}
	}
}
