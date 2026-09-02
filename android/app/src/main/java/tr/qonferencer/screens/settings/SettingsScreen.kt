package tr.qonferencer.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.PADS_NONE
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.theme.defaultBorder
import tr.qonferencer.trons.theme.defaultLayoutPadding

@Composable
fun SettingsScreen() {
	ScrollableColumn(
		modifier = Modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
		verticalArrangement = defaultSpacing,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		ChangeLanguageLayout()
		ChangeFontFamilyLayout()
		ChangeFontSizeLayout()
		ChangeAppColorsLayout()
		ChangeMealScanSoundLayout()
	}
}

/**
 * Settings entry: [introduction] text on start, [body] control on end
 * @param introduction text describing setting
 * @param body Interactive control for setting
 */
@Composable
fun SettingsCardLayout(
	introduction: String,
	body: @Composable RowScope.() -> Unit,
) {
	CardLayout(
		modifier = Modifier.defaultBorder(),
		innerPads = PADS_NONE,
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.defaultLayoutPadding(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceAround,
		) {
			Text(
				text = introduction,
				style = typo.titleMedium,
			)

			body()
		}
	}
}
