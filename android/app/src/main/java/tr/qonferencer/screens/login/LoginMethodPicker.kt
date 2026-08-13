package tr.qonferencer.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.PADS_NONE
import tr.qonferencer.trons.remembers.remember0dp
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun LoginMethodPicker(onSelect: (LoginMethod) -> Unit) {
	val localDensity = LocalDensity.current

	Text(
		text = dynamicTranslation("login.by.intro") + ":",
		style = typo.bodyLarge,
		textAlign = TextAlign.Start,
		modifier = Modifier
			.fillMaxWidth()
			.defaultLayoutPadding(Edge.START),
	)

	LoginMethod.entries.forEach { method ->
		var textBlockHeight by remember0dp()

		CardLayout(
			borderize = true,
			containerColor = colors.clickable,
			innerPads = PADS_NONE,
		) {
			Row(
				modifier = Modifier
					.clickable { onSelect(method) }
					.defaultLayoutPadding(),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Icon(
					imageVector = method.icon,
					contentDescription = method.labelKey,
					tint = colors.text,
					modifier = Modifier
						.size(textBlockHeight)
						.defaultLayoutPadding(),
				)

				Text(
					text = dynamicTranslation(method.labelKey),
					style = typo.labelLarge,
					modifier = Modifier
						.onGloballyPositioned { textBlockHeight = with(localDensity) { it.size.height.toDp() } }
						.defaultTextPadding(),
				)
			}
		}
	}
}
