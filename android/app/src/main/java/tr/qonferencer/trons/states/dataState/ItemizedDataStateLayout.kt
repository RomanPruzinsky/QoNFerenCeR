package tr.qonferencer.trons.states.dataState

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.states.StateIndicator
import tr.qonferencer.trons.states.StateSay
import tr.qonferencer.trons.states.errorIndicatorMessage

/**
 * Visualize [DataState] changes in [LazyListScope]
 *
 * @param state [DataState] to visualize
 * @param backgroundColor Background color of [StateIndicator]
 * @param textStyle [TextStyle] of [StateIndicator]
 * @param bodyOnWaiting Optional body to show when [state] is [DataState.Waiting]
 * @param bodyOnSuccess Body to show when [state] is [DataState.Success]
 *
 * @see [DataStateLayout]
 */
fun <T> LazyListScope.itemizedDataStateLayout(
	state: DataState<T>,
	backgroundColor: Color = Color.Unspecified,
	textStyle: TextStyle? = null,
	bodyOnWaiting: @Composable (() -> Unit)? = null,
	bodyOnSuccess: @Composable (T) -> Unit,
) {
	when (state) {
		is DataState.Success -> {
			item { bodyOnSuccess(state.value) }
		}

		is DataState.Error -> {
			item {
				StateIndicator(
					text = errorIndicatorMessage(state.specification),
					backgroundColor = backgroundColor,
					textStyle = textStyle ?: typo.headlineMedium,
				)
			}
		}

		DataState.Processing -> {
			item {
				StateIndicator(
					text = StateSay.PROCESSING,
					backgroundColor = backgroundColor,
					textStyle = textStyle ?: typo.headlineMedium,
				)
			}
		}

		DataState.Waiting -> {
			if (bodyOnWaiting != null) {
				item { bodyOnWaiting() }
			} else {
				item {
					StateIndicator(
						text = StateSay.WAITING,
						backgroundColor = backgroundColor,
						textStyle = textStyle ?: typo.headlineMedium,
					)
				}
			}
		}
	}
}
