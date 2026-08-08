package tr.qonferencer.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.theme.color.AppColors
import tr.qonferencer.theme.color.colors
import tr.qonferencer.trons.defaultLayouts.CustomDropdownMenu
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.remembers.rememberFalse

@Composable
fun ChangeThemeLayout() {
	SettingsCardLayout("Theme") {
		val isSelectionExpanded = rememberFalse()
		val selectedIndex =
			remember { mutableIntStateOf(QoNFerenCeRApp.themePrefs.colors.currentColors.value.ordinal) }

		CustomDropdownMenu(
			options = AppColors.entries.relist { it.name.replace("_", " ") },
			selected = selectedIndex,
			expanded = isSelectionExpanded,
			arrowAtStart = false,
			selectedColor = colors.clickable,
			additiveOnClickAction = {
				QoNFerenCeRApp.themePrefs.colors.setColors(AppColors.entries[selectedIndex.intValue])
			},
		)
	}
}
