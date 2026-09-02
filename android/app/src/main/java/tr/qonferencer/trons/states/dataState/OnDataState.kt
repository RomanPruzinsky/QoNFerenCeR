package tr.qonferencer.trons.states.dataState

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow
import tr.qonferencer.trons.states.collectValue

/** Composable to draw when [DataState] is [DataState.Waiting] */
@Composable
fun StateFlow<DataState<*>>.OnWaiting(action: @Composable () -> Unit) {
	if (this.collectValue() is DataState.Waiting) action()
}

/** Composable to draw when [DataState] is [DataState.Processing] */
@Composable
fun StateFlow<DataState<*>>.OnProcessing(action: @Composable () -> Unit) {
	if (this.collectValue() is DataState.Processing) action()
}

/** Composable to draw when [DataState] is [DataState.Error] */
@Composable
fun <T> StateFlow<DataState<T>>.OnError(action: @Composable (Exception?) -> Unit) {
	val current = this.collectValue()
	if (current is DataState.Error) action(current.specification)
}

/** Composable to draw when [DataState] is [DataState.Success] */
@Composable
fun <T> StateFlow<DataState<T>>.OnSuccess(action: @Composable (T) -> Unit) {
	val current = this.collectValue()
	if (current is DataState.Success) action(current.value)
}
