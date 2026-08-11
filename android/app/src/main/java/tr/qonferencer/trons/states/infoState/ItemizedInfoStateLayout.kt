package tr.qonferencer.trons.states.infoState

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.states.StateIndicator
import tr.qonferencer.trons.states.errorIndicatorMessage

/**
 * Visualize [InfoState] changes in [LazyListScope]
 *
 * @param state [InfoState] to visualize
 * @param backgroundColor Background color of [StateIndicator]
 * @param textStyle [TextStyle] of [StateIndicator]
 * @param bodyOnSuccess Optional body to show when [state] is [InfoState.Success]
 * @param bodyOnWaiting Body to show when [state] is [InfoState.Waiting]
 *
 * @see [InfoStateLayout]
 */
fun LazyListScope.itemizedInfoStateLayout(
	state: InfoState,
	backgroundColor: Color = Color.Unspecified,
	textStyle: TextStyle? = null,
	bodyOnSuccess: @Composable (() -> Unit)? = null,
	bodyOnWaiting: @Composable () -> Unit,
) {
	when (state) {
		InfoState.Success -> {
			item {
				if (bodyOnSuccess != null) {
					bodyOnSuccess()
				} else {
					StateIndicator(
						text = DefaultSay.SUCCESS,
						backgroundColor = backgroundColor,
						textStyle = textStyle ?: typo.headlineMedium,
					)
				}
			}
		}

		is InfoState.Error -> {
			item {
				StateIndicator(
					text = errorIndicatorMessage(state.specification),
					backgroundColor = backgroundColor,
					textStyle = textStyle ?: typo.headlineMedium,
				)
			}
		}

		InfoState.Processing -> {
			item {
				StateIndicator(
					text = DefaultSay.PROCESSING,
					backgroundColor = backgroundColor,
					textStyle = textStyle ?: typo.headlineMedium,
				)
			}
		}

		InfoState.Waiting -> {
			item { bodyOnWaiting() }
		}
	}
}
