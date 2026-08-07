package tr.qonferencer.trons.viewmodels.infoGpvm

import kotlinx.coroutines.flow.StateFlow
import tr.qonferencer.trons.states.infoState.InfoState

/**
 * Used elements in
 * - [InfoGenericParamViewModel]
 * - [InfoGenericViewModel]
 * @property state To collect [InfoState]
 * @property action To call action that return [InfoState]
 */
data class IGpVMHold(
	val state: StateFlow<InfoState>,
	val action: () -> Unit,
)
