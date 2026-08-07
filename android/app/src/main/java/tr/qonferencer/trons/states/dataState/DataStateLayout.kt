package tr.qonferencer.trons.states.dataState

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.flow.StateFlow
import tr.qonferencer.trons.states.StateIndicator
import tr.qonferencer.trons.states.StateSay
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.errorIndicatorMessage

/**
 * Visualize [DataState] changes
 *
 * @param state [DataState] to visualize
 * @param backgroundColor Background color of [StateIndicator]
 * @param textStyle [TextStyle] of [StateIndicator]
 * @param bodyOnWaiting Optional body to show when [state] is [DataState.Waiting]
 * @param bodyOnSuccess Body to show when [state] is [DataState.Success]
 */
@Composable
fun <T> DataStateLayout(
	state: DataState<T>,
	backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
	textStyle: TextStyle = MaterialTheme.typography.headlineMedium,
	bodyOnWaiting: @Composable (() -> Unit)? = null,
	bodyOnSuccess: @Composable (T) -> Unit,
) {
	when (state) {
		is DataState.Success -> {
			bodyOnSuccess(state.value)
		}

		is DataState.Error -> {
			StateIndicator(
				text = errorIndicatorMessage(state.specification),
				backgroundColor = backgroundColor,
				textStyle = textStyle,
			)
		}

		DataState.Processing -> {
			StateIndicator(
				text = StateSay.PROCESSING,
				backgroundColor = backgroundColor,
				textStyle = textStyle,
			)
		}

		DataState.Waiting -> {
			if (bodyOnWaiting != null) bodyOnWaiting()
			else {
				StateIndicator(
					text = StateSay.WAITING,
					backgroundColor = backgroundColor,
					textStyle = textStyle,
				)
			}
		}
	}
}

/**
 * Visualize [DataState] changes
 *
 * @param stateFlow [StateFlow] of [DataState] to visualize
 * @param backgroundColor Background color of [StateIndicator]
 * @param textStyle [TextStyle] of [StateIndicator]
 * @param bodyOnWaiting Optional body to show when [stateFlow] is [DataState.Waiting]
 * @param bodyOnSuccess Body to show when [stateFlow] is [DataState.Success]
 */
@Composable
fun <T> DataStateLayout(
	stateFlow: StateFlow<DataState<T>>,
	backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
	textStyle: TextStyle = MaterialTheme.typography.headlineMedium,
	bodyOnWaiting: @Composable (() -> Unit)? = null,
	bodyOnSuccess: @Composable (T) -> Unit,
) {
	DataStateLayout(
		state = stateFlow.collectValue(),
		backgroundColor = backgroundColor,
		textStyle = textStyle,
		bodyOnWaiting = bodyOnWaiting,
		bodyOnSuccess = bodyOnSuccess,
	)
}
