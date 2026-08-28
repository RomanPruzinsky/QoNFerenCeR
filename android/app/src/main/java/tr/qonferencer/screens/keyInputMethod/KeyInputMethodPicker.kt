package tr.qonferencer.screens.keyInputMethod

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.PADS_NONE
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun KeyInputMethodPicker(
	introKey: String,
	onSelect: (KeyInputMethod) -> Unit,
) {
	Text(
		text = dynamicTranslation(introKey) + ":",
		style = typo.bodyLarge,
		textAlign = TextAlign.Start,
		modifier = Modifier
			.fillMaxWidth()
			.defaultLayoutPadding(Edge.START),
	)

	KeyInputMethod.entries.forEach { method ->
		CardLayout(
			borderize = true,
			containerColor = colors.clickable,
			innerPads = PADS_NONE,
		) {
			Row(
				modifier = Modifier
					.clickable { onSelect(method) }
					.defaultLayoutPadding()
					.height(IntrinsicSize.Min),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Icon(
					imageVector = method.icon,
					contentDescription = method.labelKey,
					tint = colors.text,
					modifier = Modifier
						.fillMaxHeight()
						.aspectRatio(1f)
						.defaultLayoutPadding(),
				)

				Text(
					text = dynamicTranslation(method.labelKey),
					style = typo.labelLarge,
					modifier = Modifier
						.defaultTextPadding(),
				)
			}
		}
	}
}
