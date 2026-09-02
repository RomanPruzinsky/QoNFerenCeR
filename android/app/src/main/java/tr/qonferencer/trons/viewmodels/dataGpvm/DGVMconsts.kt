package tr.qonferencer.trons.viewmodels.dataGpvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState

/**
 * Unified [ViewModel]
 *
 * - Use [state] for [DataState] collection
 * - Use [getValue] for obtaining [DataState] action
 *
 * **Recommended** to call [getDGVMState] for usage
 *
 * @param S Type of [DataState]
 *
 * @see [getDGVMState]
 */
class DataGenericViewModel<S> : ViewModel() {

	private val _state = initDataState<S>()

	/** [DataState] to collect */
	val state: StateFlow<DataState<S>> = _state.asStateFlow()

	/**
	 * Action to obtain [DataState]
	 *
	 * Collect result with [state]
	 *
	 * @param action Action to obtain [DataState]
	 * @see [dataStatedAction]
	 */
	fun getValue(action: suspend ViewModel.() -> S) {
		dataStatedAction(_state) { action() }
	}
}

/**
 * Get [DataGenericViewModel]'s:
 * - state to collect
 * - function to call to obtain value
 *
 * ```
 * val vm = getDGVMState(INIT_GPvM) { retrofit.getFun(funArgs) }
 * val state = vm.state.collectValue()
 * Modifier.clickable { vm.action() }
 * ```
 *
 * @param S Type of [DataState]
 * @param key Identifier
 * @param initAction Whether to call [action] on init
 * @param action Action to obtain [DataState]
 *
 * @return [DGpVMHold] with [DataState] and [action]
 */
@Composable
fun <S> getDGVMState(
	key: String? = null,
	initAction: Boolean = false,
	action: suspend ViewModel.() -> S,
): DGpVMHold<S> {
	val vm = viewModel<DataGenericViewModel<S>>(key = key)

	if (initAction) LaunchedEffect(Unit) { vm.getValue(action) }

	return DGpVMHold(vm.state) { vm.getValue(action) }
}
