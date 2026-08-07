package tr.qonferencer.trons.states.dataState

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import tr.qonferencer.trons.viewmodels.corout

/**
 * Executes full [DataState] process:
 * - [DataState.Waiting]
 * - [corout]
 * - [DataState.Processing]
 * - action()
 * - [DataState.Success] / [DataState.Error] (try-catched)
 *
 * @param T Type of data to get
 * @param state [MutableStateFlow] of [DataState]
 * @param action Action to execute to get data [T]
 */
fun <T> ViewModel.dataStatedAction(state: MutableStateFlow<DataState<T>>, action: suspend ViewModel.() -> T) {
	corout {
		state.processing()
		try {
			state.success(action())
		} catch (e: Exception) {
			e.printStackTrace()
			state.error(e)
		}
	}
}
