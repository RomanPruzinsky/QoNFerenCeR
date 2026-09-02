package tr.qonferencer.trons.states.dataState

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import tr.qonferencer.trons.miscs.DEFAULT_STATE_CHANGE_DELAY_SECS
import tr.qonferencer.trons.viewmodels.corout
import kotlin.time.Duration

/**
 * Executes full [DataState] process:
 * - [DataState.Waiting]
 * - [corout]
 * - [DataState.Processing]
 * - action()
 * - [DataState.Success] / [DataState.Error] (try-catched)
 * - if [onErrorShouldReset]: delay([delayTime]), then back to [DataState.Waiting]
 *
 * @param T Type of data to get
 * @param state [MutableStateFlow] of [DataState]
 * @param delayTime Delay before resetting to [DataState.Waiting] after error
 * @param onErrorShouldReset Whether to reset to [DataState.Waiting] after error
 * @param action Action to execute to get data [T]
 */
fun <T> ViewModel.dataStatedAction(
	state: MutableStateFlow<DataState<T>>,
	delayTime: Duration = DEFAULT_STATE_CHANGE_DELAY_SECS,
	onErrorShouldReset: Boolean = true,
	action: suspend ViewModel.() -> T,
) {
	corout {
		state.processing()
		try {
			state.success(action())
		} catch (e: Exception) {
			e.printStackTrace()
			state.error(e)
			if (onErrorShouldReset) {
				delay(delayTime)
				state.waiting()
			}
		}
	}
}
