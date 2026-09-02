package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.theme.defaultBorder
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultTextPadding

/**
 * If [opened]: shows [DialogFullWidth] with [message] and confirm/cancel choices
 * @param opened Whether dialog is shown, closed on either choice
 * @param message Text asking what's being confirmed
 * @param onConfirm Called, then dialog closes, on confirm choice
 */
@Composable
fun ConfirmDialog(
	opened: MutableState<Boolean>,
	message: String,
	onConfirm: () -> Unit,
) {
	if (!opened.value) return
	
	DialogFullWidth(onDismissRequestAction = { opened.value = false }) {
		CardLayout {
			Text(
				text = message,
				style = typo.titleMedium.copy(textAlign = TextAlign.Start),
				modifier = Modifier.defaultTextPadding(),
			)
			
			DefaultHeightSpacer()
			
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = defaultSpacing,
			) {
				Text(
					text = dynamicTranslation("misc.confirm"),
					style = typo.labelLarge,
					modifier = Modifier
						.defaultBorder()
						.background(colors.action.approve)
						.clickable {
							opened.value = false
							onConfirm()
						}
						.defaultTextPadding(),
				)
				
				Text(
					text = dynamicTranslation("misc.cancel"),
					style = typo.labelLarge,
					color = colors.text,
					modifier = Modifier
						.defaultClip()
						.background(colors.action.delete)
						.clickable { opened.value = false }
						.defaultTextPadding(),
				)
			}
		}
	}
}
