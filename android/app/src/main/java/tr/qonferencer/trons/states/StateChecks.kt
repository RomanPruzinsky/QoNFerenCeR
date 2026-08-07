package tr.qonferencer.trons.states

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

/*
 * Unified state-check extensions over [StateBase], covering both DataState and InfoState
 * via marker interfaces. Replaces the previously-duplicated dataStateCheck.kt and
 * InfoStateCheck.kt files.
 *
 * Receiver types:
 *  - StateBase                  — direct check on a state value
 *  - StateFlow<StateBase>   — checks .value on the flow
 *  - StateFlow<StateBase>   — keepsX collects the flow as composable state
 */

//////////////////////////////////////
////////////// WAITING ///////////////

/** Whether this state is [StateBase.Waiting] */
fun StateBase.isWaiting() = this is StateBase.Waiting

/** Whether this state is NOT [StateBase.Waiting] */
fun StateBase.isNotWaiting() = !isWaiting()

/** Whether the flow's current value is [StateBase.Waiting] */
fun StateFlow<StateBase>.isWaiting() = this.value.isWaiting()

/** Whether the flow's current value is NOT [StateBase.Waiting] */
fun StateFlow<StateBase>.isNotWaiting() = !this.isWaiting()

/** Composable check — recomposes when flow emits */
@Composable
fun StateFlow<StateBase>.keepsWaiting() = this.collectValue().isWaiting()

////////////// WAITING ///////////////
//////////////////////////////////////
//////////// PROCESSING //////////////

/** Whether this state is [StateBase.Processing] */
fun StateBase.isProcessing() = this is StateBase.Processing

/** Whether this state is NOT [StateBase.Processing] */
fun StateBase.isNotProcessing() = !isProcessing()

/** Whether the flow's current value is [StateBase.Processing] */
fun StateFlow<StateBase>.isProcessing() = this.value.isProcessing()

/** Whether the flow's current value is NOT [StateBase.Processing] */
fun StateFlow<StateBase>.isNotProcessing() = !this.isProcessing()

/** Composable check — recomposes when flow emits */
@Composable
fun StateFlow<StateBase>.keepsProcessing() = this.collectValue().isProcessing()

//////////// PROCESSING //////////////
//////////////////////////////////////
/////////////// ERROR ////////////////

/** Whether this state is [StateBase.Error] */
fun StateBase.isError() = this is StateBase.Error

/** Whether this state is NOT [StateBase.Error] */
fun StateBase.isNotError() = !isError()

/** Whether the flow's current value is [StateBase.Error] */
fun StateFlow<StateBase>.isError() = this.value.isError()

/** Whether the flow's current value is NOT [StateBase.Error] */
fun StateFlow<StateBase>.isNotError() = !this.isError()

/** Composable check — recomposes when flow emits */
@Composable
fun StateFlow<StateBase>.keepsError() = this.collectValue().isError()

/////////////// ERROR ////////////////
//////////////////////////////////////
////////////// SUCCESS ///////////////

/** Whether this state is [StateBase.Success] */
fun StateBase.isSuccess() = this is StateBase.Success

/** Whether this state is NOT [StateBase.Success] */
fun StateBase.isNotSuccess() = !isSuccess()

/** Whether the flow's current value is [StateBase.Success] */
fun StateFlow<StateBase>.isSuccess() = this.value.isSuccess()

/** Whether the flow's current value is NOT [StateBase.Success] */
fun StateFlow<StateBase>.isNotSuccess() = !this.isSuccess()

/** Composable check — recomposes when flow emits */
@Composable
fun StateFlow<StateBase>.keepsSuccess() = this.collectValue().isSuccess()

////////////// SUCCESS ///////////////
//////////////////////////////////////
