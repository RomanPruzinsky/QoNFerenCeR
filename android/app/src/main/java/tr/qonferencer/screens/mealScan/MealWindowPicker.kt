package tr.qonferencer.screens.mealScan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tr.qonferencer.shared.dtos.MealWindowDto
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.DialogFullWidth
import tr.qonferencer.trons.defaultLayouts.PADS_DEFAULT
import tr.qonferencer.trons.defaultLayouts.PADS_TEXT
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding
import tr.qonferencer.trons.theme.orTransparentIf

/**
 * Current meal window - tap to auto-pick actual, long-press to pick from [windows]
 * @param currentWindow Currently picked window
 * @param windows All windows to choose from in dialog
 * @param onPickCurrent Picks window open right now
 * @param onPickWindow Picks specific window
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MealWindowPicker(
	currentWindow: MealWindowDto,
	windows: List<MealWindowDto>,
	onPickCurrent: () -> Unit,
	onPickWindow: (MealWindowDto) -> Unit,
) {
	var showDialog by rememberFalse()
	
	if (showDialog) {
		MealWindowDialog(
			windows = windows,
			currentWindow = currentWindow,
			onPickWindow = {
				onPickWindow(it)
				showDialog = false
			},
			closeDialog = { showDialog = false },
		)
	}
	
	CardLayout(
		modifier = Modifier
			.defaultClip()
			.combinedClickable(
				onClick = onPickCurrent,
				onLongClick = { showDialog = true },
			),
		innerPads = PADS_TEXT,
		outerPads = PADS_DEFAULT,
	) {
		Text(
			text = dynamicTranslation(currentWindow.nameKey),
			style = typo.titleMedium,
		)
	}
}

@Composable
private fun MealWindowDialog(
	windows: List<MealWindowDto>,
	currentWindow: MealWindowDto,
	onPickWindow: (MealWindowDto) -> Unit,
	closeDialog: () -> Unit,
) {
	DialogFullWidth(onDismissRequestAction = closeDialog) {
		ScrollableColumn(
			modifier = Modifier
				.defaultClip()
				.background(colors.element)
				.defaultLayoutPadding(multiplier = 2F),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = defaultSpacing,
		) {
			windows.forEach { window ->
				Text(
					text = dynamicTranslation(window.nameKey),
					style = typo.labelLarge,
					modifier = Modifier
						.defaultClip()
						.background(colors.selected.orTransparentIf(currentWindow != window))
						.clickable { onPickWindow(window) }
						.defaultTextPadding(),
				)
			}
		}
	}
}
