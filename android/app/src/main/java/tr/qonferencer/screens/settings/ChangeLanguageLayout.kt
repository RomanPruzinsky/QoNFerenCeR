package tr.qonferencer.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.theme.color.colors
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CustomDropdownMenu
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.states.collectValue

@Composable
fun ChangeLanguageLayout() {
	SettingsCardLayout(dynamicTranslation("settings.language.intro")) {
		val availableLanguages = QoNFerenCeRApp.language.options.collectValue().languages
		val isSelectionExpanded = rememberFalse()
		val selectedIndex = remember {
			val currentCode = QoNFerenCeRApp.language.current.value
			mutableIntStateOf(availableLanguages.indexOfFirst { it.code == currentCode }.coerceAtLeast(0))
		}
		
		CustomDropdownMenu(
			options = availableLanguages.relist { it.name },
			selected = selectedIndex,
			expanded = isSelectionExpanded,
			arrowAtStart = false,
			selectedColor = colors.clickable,
			additiveOnClickAction = {
				availableLanguages.getOrNull(selectedIndex.intValue)?.let {
					QoNFerenCeRApp.language.select(it.code)
				}
			},
		)
	}
}
