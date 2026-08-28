package tr.qonferencer.trons.viewmodels.infoGpvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.trons.states.infoState.InfoState
import tr.qonferencer.trons.states.infoState.infoStatedAction
import tr.qonferencer.trons.states.infoState.initInfoState

/**
 * Unified [ViewModel] with 1 parameter
 *
 * - Use [state] for [InfoState] collection
 * - Use [getValue] for obtaining [InfoState] action
 *
 * **Recommended** to call [getIGPVMState] for usage
 *
 * @param P Type of key
 * @param key Key parameter, can be used in [getValue]
 *
 * @see [getIGPVMState]
 */
class InfoGenericParamViewModel<P>(
	private val key: P,
) : ViewModel() {

	private val _state = initInfoState()

	/** [InfoState] to collect */
	val state: StateFlow<InfoState> = _state.asStateFlow()

	/**
	 * Action to obtain [InfoState]
	 *
	 * Collect result with [state]
	 *
	 * @param A Type of arguments
	 *
	 * @param args Arguments for [action]
	 * @param delayedAction Action to return, see more in [infoStatedAction]
	 * @param action Action to obtain [InfoState]
	 *
	 *  @see [infoStatedAction]
	 */
	fun <A> getValue(
		args: A,
		delayedAction: suspend ViewModel.() -> Unit = {},
		action: suspend ViewModel.(P, A) -> Unit,
	) {
		infoStatedAction(
			state = _state,
			delayedAction = delayedAction,
			action = { action(key, args) },
		)
	}
}

/**
 * Get [InfoGenericParamViewModel]'s:
 * - state to collect
 * - function to call to obtain value
 *
 * ```
 * val vm = getIGPVMState(
 *    param = idParam,
 *    args = someArgument,
 * ) { id, arg ->
 *    retrofit.getFun(id, arg)
 * }
 * val state = vm.state.collectValue()
 * Modifier.clickable { vm.action() }
 * ```
 *
 * @param P Type of key
 * @param A Type of arguments
 *
 * @param param Key parameter, can be used in [action]
 * @param initAction Whether to call [action] on init
 * @param args Arguments for [action]
 * @param delayedAction Action to return, see more in [infoStatedAction]
 * @param action Action to obtain [InfoState]
 *
 * @return [IGpVMHold] with [InfoState] and [action]
 */
@Composable
fun <P, A> getIGPVMState(
	param: P,
	args: A,
	initAction: Boolean = false,
	delayedAction: suspend ViewModel.() -> Unit = {},
	action: suspend ViewModel.(P, A) -> Unit,
): IGpVMHold {
	val vm = viewModel<InfoGenericParamViewModel<P>>(
		key = param.toString(),
		factory = viewModelFactory {
			initializer { InfoGenericParamViewModel<P>(param) }
		},
	)

	if (initAction) LaunchedEffect(Unit) {
		vm.getValue(args = args, delayedAction = delayedAction, action = action)
	}

	return IGpVMHold(vm.state) {
		vm.getValue(args = args, delayedAction = delayedAction, action = action)
	}
}
