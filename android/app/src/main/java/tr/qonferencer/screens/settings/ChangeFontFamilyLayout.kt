package tr.qonferencer.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.theme.AvailableFonts
import tr.qonferencer.theme.colors
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CustomDropdownMenu
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.remembers.rememberFalse

@Composable
fun ChangeFontFamilyLayout() {
	SettingsCardLayout(dynamicTranslation("settings.fontFamily")) {
		val isSelectionExpanded = rememberFalse()
		val selectedIndex =
			remember { mutableIntStateOf(QoNFerenCeRApp.themePrefs.font.currentFont.value.ordinal) }

		CustomDropdownMenu(
			options = AvailableFonts.entries.relist { it.name.replace("_", " ") },
			selected = selectedIndex,
			expanded = isSelectionExpanded,
			specialFont = AvailableFonts.entries.relist { it.family },
			selectedColor = colors.clickable,
			additiveOnClickAction = {
				QoNFerenCeRApp.themePrefs.font.setFont(AvailableFonts.entries[selectedIndex.intValue])
			},
		)
	}
}
