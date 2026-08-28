package tr.qonferencer.trons.defaultLayouts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.ops.orEmptyIf
import tr.qonferencer.trons.remembers.switch
import tr.qonferencer.trons.theme.defaultBorder
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultTextPadding
import tr.qonferencer.trons.theme.halfDefaultLayoutPadding

/**
 * Shows [AnimatedArrows] and rollable [options] with selected item indicator
 *
 * @param options Strings to display
 * @param selected Selected item index
 * @param expanded Whether menu is expanded
 * @param arrowAtStart Whether arrow is at start or end of text
 * @param specialFont Whether to show [options] in special font
 * @param selectedColor Background of selected item's toggle
 * @param additiveOnClickAction Action executed on click, on top of updating [selected]/[expanded]
 *
 * @see [AnimatedArrows]
 */
@Composable
fun CustomDropdownMenu(
	options: List<String>,
	selected: MutableState<Int>,
	expanded: MutableState<Boolean>,
	arrowAtStart: Boolean,
	specialFont: List<FontFamily>? = null,
	selectedColor: Color = colors.selected,
	additiveOnClickAction: () -> Unit = {},
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(halfDefaultLayoutPadding),
	) {
		AnimatedArrows(
			isOpen = expanded.value,
			openedIndicator = getTextForCustomDropdownMenuTopValue(
				directionUp = true,
				text = options[selected.value],
				arrowAtStart = arrowAtStart,
			),
			closedIndicator = getTextForCustomDropdownMenuTopValue(
				directionUp = false,
				text = options[selected.value],
				arrowAtStart = arrowAtStart,
			),
			textStyle = typo.labelMedium,
			textModifier = Modifier
				.defaultClip()
				.background(selectedColor)
				.clickable { expanded.switch() }
				.defaultTextPadding(),
		)

		AnimatedVisibility(visible = expanded.value) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(halfDefaultLayoutPadding),
			) {
				options.forEachIndexed { index, option ->
					if (index != selected.value) {
						Text(
							text = option,
							style = if (specialFont != null) {
								typo.bodyMedium.copy(fontFamily = specialFont[index])
							} else {
								typo.bodyMedium
							},
							modifier = Modifier
								.defaultClip()
								.defaultBorder()
								.clickable {
									selected.value = index
									expanded.value = false
									additiveOnClickAction()
								}
								.defaultTextPadding(),
						)
					}
				}
			}
		}
	}
}

/**
 * Gets oriented arrow: [AnimatedArrowsSay.ARROW_UP] / [AnimatedArrowsSay.ARROW_DOWN]
 * @param directionUp Whether arrow is pointing up or down
 * @return Arrow oriented by [directionUp]
 */
private fun getOrientedArrow(directionUp: Boolean) =
	AnimatedArrowsSay.ARROW_UP.takeIf { directionUp } ?: AnimatedArrowsSay.ARROW_DOWN

/**
 * Gets oriented arrow with specified text
 * @param directionUp Whether arrow is pointing up or down
 * @param arrowAtStart Whether returned String is "arrow| text" (== true) or "text |arrow" (== false)
 * @return Arrow oriented by [directionUp] with [text] ordered by [arrowAtStart]
 * @see [getOrientedArrow]
 */
private fun getTextForCustomDropdownMenuTopValue(
	directionUp: Boolean,
	arrowAtStart: Boolean,
	text: String,
) = "${getOrientedArrow(directionUp)}| ".orEmptyIf(!arrowAtStart) + text +
	" |${getOrientedArrow(directionUp)}".orEmptyIf(arrowAtStart)
