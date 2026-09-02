package tr.qonferencer.trons.states.infoState

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.flow.StateFlow
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.states.StateIndicator
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.errorIndicatorMessage

/**
 * Visualize [InfoState] changes
 *
 * @param state [InfoState] to visualize
 * @param backgroundColor Background color of [StateIndicator]
 * @param textStyle [TextStyle] of [StateIndicator]
 * @param bodyOnSuccess Optional body to show when [state] is [InfoState.Success]
 * @param bodyOnWaiting Body to show when [state] is [InfoState.Waiting]
 */
@Composable
fun InfoStateLayout(
	state: InfoState,
	backgroundColor: Color = colors.element,
	textStyle: TextStyle = typo.headlineMedium,
	bodyOnSuccess: @Composable (() -> Unit)? = null,
	bodyOnWaiting: @Composable () -> Unit,
) {
	when (state) {
		InfoState.Success -> {
			if (bodyOnSuccess != null) bodyOnSuccess()
			else {
				StateIndicator(
					text = DefaultSay.SUCCESS,
					backgroundColor = backgroundColor,
					textStyle = textStyle,
				)
			}
		}
		
		is InfoState.Error -> {
			StateIndicator(
				text = errorIndicatorMessage(state.specification),
				backgroundColor = backgroundColor,
				textStyle = textStyle,
			)
		}
		
		InfoState.Processing -> CircularProgressIndicator()
		
		InfoState.Waiting -> {
			bodyOnWaiting()
		}
	}
}

/**
 * Visualize [InfoState] changes
 *
 * @param stateFlow [StateFlow] of [InfoState] to visualize
 * @param backgroundColor Background color of [StateIndicator]
 * @param textStyle [TextStyle] of [StateIndicator]
 * @param bodyOnSuccess Optional body to show when [stateFlow] is [InfoState.Success]
 * @param bodyOnWaiting Body to show when [stateFlow] is [InfoState.Waiting]
 */
@Composable
fun InfoStateLayout(
	stateFlow: StateFlow<InfoState>,
	backgroundColor: Color = colors.element,
	textStyle: TextStyle = typo.headlineMedium,
	bodyOnSuccess: @Composable (() -> Unit)? = null,
	bodyOnWaiting: @Composable () -> Unit,
) {
	InfoStateLayout(
		state = stateFlow.collectValue(),
		backgroundColor = backgroundColor,
		textStyle = textStyle,
		bodyOnWaiting = bodyOnWaiting,
		bodyOnSuccess = bodyOnSuccess,
	)
}
