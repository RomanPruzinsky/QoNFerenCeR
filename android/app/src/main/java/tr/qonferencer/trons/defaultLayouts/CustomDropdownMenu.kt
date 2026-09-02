package tr.qonferencer.trons.defaultLayouts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.remembers.switch
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultBorder
import tr.qonferencer.trons.theme.defaultBorderSize
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding
import tr.qonferencer.trons.theme.halfDefaultLayoutPadding
import tr.qonferencer.trons.theme.specPadding

/** Which side of the selected text [CustomDropdownMenu]'s arrow indicator sits on */
enum class ArrowPosition { START, END }

/**
 * Shows [AnimatedArrows] and rollable [options] with selected item indicator
 *
 * @param options Strings to display
 * @param selected Selected item index
 * @param expanded Whether menu is expanded
 * @param specialFont Whether to show [options] in special font
 * @param arrowPosition Whether arrow sits at [ArrowPosition.START] or [ArrowPosition.END] of text
 * @param useDivider Whether to show vertical divider between text and arrow, `false` shows [defaultLayoutPadding] space instead
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
	specialFont: List<FontFamily>? = null,
	arrowPosition: ArrowPosition = ArrowPosition.END,
	useDivider: Boolean = false,
	selectedColor: Color = colors.selected,
	additiveOnClickAction: () -> Unit = {},
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(halfDefaultLayoutPadding),
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.height(IntrinsicSize.Min)
				.defaultClip()
				.background(selectedColor)
				.clickable { expanded.switch() }
				.defaultTextPadding(),
		) {
			@Composable
			fun Arrow() = AnimatedArrows(
				isOpen = expanded.value,
				textStyle = typo.labelMedium,
			)

			@Composable
			fun Separator() {
				if (useDivider) {
					VerticalDivider(
						modifier = Modifier
							.fillMaxHeight()
							.specPadding(Edge.HORIZONTAL to halfDefaultLayoutPadding),
						color = colors.text,
						thickness = defaultBorderSize,
					)
				} else {
					Spacer(modifier = Modifier.width(defaultLayoutPadding))
				}
			}

			when (arrowPosition) {
				ArrowPosition.START -> {
					Arrow()
					Separator()
					Text(text = options[selected.value], style = typo.labelMedium)
				}

				ArrowPosition.END -> {
					Text(text = options[selected.value], style = typo.labelMedium)
					Separator()
					Arrow()
				}
			}
		}

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
