package tr.qonferencer.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tr.qonferencer.shared.CustomDataType
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.ProfileDisplayRow
import tr.qonferencer.trons.ops.orNullIf
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultTextPadding
import tr.qonferencer.trons.theme.halfDefaultLayoutPadding

/** One editable customData row, kept as stable [mutableStateOf] pair for [DefaultOTF] */
class CustomDataFieldState(
	key: String = "",
	value: String = "",
) {
	val key = mutableStateOf(key)
	val value = mutableStateOf(value)
}

/** @return [CustomDataType] built from [this], dropping rows with blank key */
fun List<CustomDataFieldState>.toCustomData(): CustomDataType = this.filter { it.key.value.isNotBlank() }
	.associate { it.key.value to it.value.value }

/** @return [this] as editable [CustomDataFieldState] rows */
fun CustomDataType.toFieldStates(): List<CustomDataFieldState> =
	this.map { (key, value) -> CustomDataFieldState(key, value?.toString().orEmpty()) }

/**
 * Editable free-form key/value rows backing [CustomDataType]
 * @param fields Rows to edit, mutated in place by add/remove/rename
 */
@Composable
fun CustomDataEditor(fields: SnapshotStateList<CustomDataFieldState>) {
	fields.forEach { field ->
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(halfDefaultLayoutPadding),
			verticalAlignment = Alignment.CenterVertically,
		) {
			DefaultOTF(
				valueText = field.key,
				labelText = dynamicTranslation("admin.customData.key"),
				modifier = Modifier.weight(1F),
			)
			DefaultOTF(
				valueText = field.value,
				labelText = dynamicTranslation("admin.customData.value"),
				modifier = Modifier.weight(1F),
			)
			Icon(
				imageVector = Icons.Default.Delete,
				contentDescription = "remove field",
				tint = colors.text,
				modifier = Modifier
					.defaultClip()
					.clickable { fields.remove(field) }
					.defaultTextPadding()
					.size(defaultIconSize),
			)
		}
	}
	
	Text(
		text = "+ " + dynamicTranslation("admin.customData.addField"),
		style = typo.labelLarge,
		modifier = Modifier
			.defaultClip()
			.background(colors.clickable)
			.clickable { fields.add(CustomDataFieldState()) }
			.defaultTextPadding(),
	)
}

/** Read-only display of [customData] as [ProfileDisplayRow]s */
@Composable
fun CustomDataDisplay(customData: CustomDataType) {
	customData.forEach { (key, value) ->
		ProfileDisplayRow(
			label = key,
			value = value?.toString().orNullIf { it?.isBlank() ?: true } ?: "-",
		)
	}
}
