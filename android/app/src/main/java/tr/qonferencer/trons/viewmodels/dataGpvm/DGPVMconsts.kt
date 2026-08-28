package tr.qonferencer.trons.viewmodels.dataGpvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState

/**
 * Unified [ViewModel] with 1 parameter
 *
 * - Use [state] for [DataState] collection
 * - Use [getValue] for obtaining [DataState] action
 *
 * **Recommended** to call [getDGPVMState] for usage
 *
 * @param P Type of key
 * @param S Type of [DataState]
 * @param key Key parameter, can be used in [getValue]
 *
 * @see [getDGPVMState]
 */
class DataGenericParamViewModel<P, S>(
	private val key: P,
) : ViewModel() {

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
	fun getValue(action: suspend ViewModel.(P) -> S) {
		dataStatedAction(_state) { action(key) }
	}
}

/**
 * Get [DataGenericParamViewModel]'s:
 * - state to collect
 * - function to call to obtain value
 *
 * ```
 * val vm = getDGPVMState(idParam, INIT_GPvM) { retrofit.getFun(funArgs) }
 * val state = vm.state.collectValue()
 * Modifier.clickable { vm.action() }
 * ```
 *
 * @param P Type of key
 * @param S Type of [DataState]
 *
 * @param param Key parameter, can be used in [action]
 * @param initAction Whether to call [action] on init
 * @param action Action to obtain [DataState]
 *
 * @return [DGpVMHold] with [DataState] and [action]
 */
@Composable
fun <P, S> getDGPVMState(
	param: P,
	initAction: Boolean = false,
	action: suspend ViewModel.(P) -> S,
): DGpVMHold<S> {
	val vm = viewModel<DataGenericParamViewModel<P, S>>(
		key = param.toString(),
		factory = viewModelFactory {
			initializer { DataGenericParamViewModel<P, S>(param) }
		},
	)

	if (initAction) LaunchedEffect(Unit) { vm.getValue(action) }

	return DGpVMHold(vm.state) { vm.getValue(action) }
}
