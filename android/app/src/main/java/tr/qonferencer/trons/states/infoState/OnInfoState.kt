package tr.qonferencer.trons.states.infoState

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow
import tr.qonferencer.trons.states.collectValue

/** Composable to draw when [InfoState] is [InfoState.Waiting] */
@Composable
fun StateFlow<InfoState>.OnWaiting(action: @Composable () -> Unit) {
	if (this.collectValue() is InfoState.Waiting) action()
}

/** Composable to draw when [InfoState] is [InfoState.Processing] */
@Composable
fun StateFlow<InfoState>.OnProcessing(action: @Composable () -> Unit) {
	if (this.collectValue() is InfoState.Processing) action()
}

/** Composable to draw when [InfoState] is [InfoState.Error] */
@Composable
fun StateFlow<InfoState>.OnError(action: @Composable (Exception?) -> Unit) {
	val current = this.collectValue()
	if (current is InfoState.Error) action(current.specification)
}

/** Composable to draw when [InfoState] is [InfoState.Success] */
@Composable
fun StateFlow<InfoState>.OnSuccess(action: @Composable () -> Unit) {
	if (this.collectValue() is InfoState.Success) action()
}
