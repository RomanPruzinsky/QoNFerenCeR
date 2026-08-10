package tr.qonferencer.trons.defaultLayouts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import tr.qonferencer.theme.typo

/**
 * Display animated text (defaultly arrows) indicating opened/closed menu.
 *
 * @param isOpen Boolean state determining which indicator to show. `true` for [openedIndicator], `false` for [closedIndicator].
 * @param openedIndicator String representing opened state.
 * @param closedIndicator String representing closed state.
 * @param textStyle [TextStyle] applied to indicator text.
 * @param modifier [Modifier] applied to parent.
 * @param textModifier [Modifier] applied to indicator text.
 */
@Composable
fun AnimatedArrows(
	isOpen: Boolean,
	modifier: Modifier = Modifier,
	textModifier: Modifier = Modifier,
	openedIndicator: String = AnimatedArrowsSay.ARROW_UP,
	closedIndicator: String = AnimatedArrowsSay.ARROW_DOWN,
	textStyle: TextStyle = typo.bodyMedium,
) {
	Box(modifier = modifier) {
		@Composable
		fun AnimatedVisibilityText(should: Boolean) {
			Row {
				AnimatedVisibility(
					visible =
					if (should) isOpen
					else !isOpen,
					enter = fadeIn(),
					exit = fadeOut(),
				) {
					Text(
						text =
						if (should) openedIndicator
						else closedIndicator,
						style = textStyle,
						modifier = textModifier,
					)
				}
			}
		}
		
		AnimatedVisibilityText(true)
		AnimatedVisibilityText(false)
	}
}

object AnimatedArrowsSay {
	const val ARROW_UP = "▲"
	const val ARROW_DOWN = "▼"
}
