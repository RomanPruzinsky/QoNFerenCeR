package tr.qonferencer.trons.viewmodels.dataGpvm

import kotlinx.coroutines.flow.StateFlow
import tr.qonferencer.trons.states.dataState.DataState

/**
 * Used elements in
 * - [DataGenericParamViewModel]
 * - [DataGenericViewModel]
 * @property state To collect [DataState]
 * @property action To call action that return [DataState]
 */
data class DGpVMHold<S>(
	val state: StateFlow<DataState<S>>,
	val action: () -> Unit,
)
