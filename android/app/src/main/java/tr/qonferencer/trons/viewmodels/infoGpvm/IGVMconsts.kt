package tr.qonferencer.trons.viewmodels.infoGpvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.trons.states.infoState.InfoState
import tr.qonferencer.trons.states.infoState.infoStatedAction
import tr.qonferencer.trons.states.infoState.initInfoState

/**
 * Unified [ViewModel]
 *
 * - Use [state] for [InfoState] collection
 * - Use [getValue] for obtaining [InfoState] action
 *
 * **Recommended** to call [getIGVMState] for usage
 *
 * @see [getIGVMState]
 */
class InfoGenericViewModel : ViewModel() {

	private val _state = initInfoState()

	/** [InfoState] to collect */
	val state: StateFlow<InfoState> = _state.asStateFlow()

	/**
	 * Action to obtain [InfoState]
	 *
	 * Collect result with [state]
	 *
	 * @param A Type of arguments
	 * @param args Arguments for [action]
	 * @param delayedAction Action to return, see more in [infoStatedAction]
	 * @param action Action to obtain [InfoState]
	 *
	 * @see [infoStatedAction]
	 */
	fun <A> getValue(
		args: A,
		delayedAction: suspend ViewModel.() -> Unit = {},
		action: suspend ViewModel.(A) -> Unit,
	) {
		infoStatedAction(
			state = _state,
			delayedAction = delayedAction,
			action = { action(args) },
		)
	}
}

/**
 * Get [InfoGenericViewModel]'s:
 * - state to collect
 * - function to call to obtain value
 *
 * ```
 * val vm = getIGVMState(
 *    args = someArgument,
 * ) { arg ->
 *    retrofit.getFun(arg)
 * }
 * val state = vm.state.collectValue()
 * Modifier.clickable { vm.action() }
 * ```
 *
 * @param A Type of arguments
 *
 * @param key Identifier
 * @param args Arguments for [action]
 * @param initAction Whether to call [action] on init
 * @param delayedAction Action to return, see more in [infoStatedAction]
 * @param action Action to obtain [InfoState]
 *
 * @return [IGpVMHold] with [InfoState] and [action]
 */
@Composable
fun <A> getIGVMState(
	args: A,
	key: String? = null,
	initAction: Boolean = false,
	delayedAction: suspend ViewModel.() -> Unit = {},
	action: suspend ViewModel.(A) -> Unit,
): IGpVMHold {
	val vm = viewModel<InfoGenericViewModel>(key = key)

	if (initAction) LaunchedEffect(Unit) {
		vm.getValue(args = args, delayedAction = delayedAction, action = action)
	}

	return IGpVMHold(vm.state) {
		vm.getValue(args = args, delayedAction = delayedAction, action = action)
	}
}
