package tr.qonferencer.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.theme.AppColorsOptions
import tr.qonferencer.theme.colors
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CustomDropdownMenu
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.remembers.rememberFalse

@Composable
fun ChangeAppColorsLayout() {
	SettingsCardLayout(dynamicTranslation("settings.appColors")) {
		val isSelectionExpanded = rememberFalse()
		val selectedIndex =
			remember { mutableIntStateOf(QoNFerenCeRApp.themePrefs.colors.currentColors.value.ordinal) }

		CustomDropdownMenu(
			options = AppColorsOptions.entries.relist { it.name.replace("_", " ") },
			selected = selectedIndex,
			expanded = isSelectionExpanded,
			selectedColor = colors.clickable,
			additiveOnClickAction = {
				QoNFerenCeRApp.themePrefs.colors.setColors(AppColorsOptions.entries[selectedIndex.intValue])
			},
		)
	}
}
