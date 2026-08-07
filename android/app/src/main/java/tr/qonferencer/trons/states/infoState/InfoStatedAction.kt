package tr.qonferencer.trons.states.infoState

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import tr.qonferencer.trons.miscs.BasicResponse
import tr.qonferencer.trons.miscs.DEFAULT_STATE_CHANGE_DELAY_SECS
import tr.qonferencer.trons.viewmodels.corout
import kotlin.time.Duration

/**
 * Executes full [InfoState] process:
 * - [InfoState.Waiting]
 * - [corout]
 * - [InfoState.Processing]
 * - action()
 * - [InfoState.Success] / [InfoState.Error] (try-catched)
 * ---
 * - delay([delayTime])
 * - [delayedAction]
 * - [InfoState.Waiting]
 *
 * @param state [MutableStateFlow] of [InfoState]
 * @param delayTime Delay time before [delayedAction] and [InfoState.Waiting]
 * @param delayedAction Action to execute after [delayTime] and before [InfoState.Waiting]
 * @param action Action to execute
 */
fun ViewModel.infoStatedAction(
	state: MutableStateFlow<InfoState>,
	delayTime: Duration = DEFAULT_STATE_CHANGE_DELAY_SECS,
	delayedAction: suspend ViewModel.() -> Unit = {},
	action: suspend ViewModel.() -> Unit,
) {
	corout {
		state.processing()
		try {
			action()
			state.success()
		} catch (e: Exception) {
			e.printStackTrace()
			state.error(e)
		}

		delay(delayTime)
		delayedAction()
		state.waiting()
	}
}

/**
 * Executes full [InfoState] process with BasicResponse checking:
 * - [InfoState.Waiting]
 * - [corout]
 * - [InfoState.Processing]
 * - action()
 * - - if **action != [BasicResponse.SUCCESS]** throw Exception
 * - [InfoState.Success] / [InfoState.Error] (try-catched)
 * ---
 * - delay([delayTime])
 * - [delayedAction]
 * - [InfoState.Waiting]
 *
 * @param state [MutableStateFlow] of [InfoState]
 * @param delayTime Delay time before [delayedAction] and [InfoState.Waiting]
 * @param delayedAction Action to execute after [delayTime] and before [InfoState.Waiting]
 * @param action Action to execute
 */
fun ViewModel.infoStatedAction4BasicResponse(
	state: MutableStateFlow<InfoState>,
	delayTime: Duration = DEFAULT_STATE_CHANGE_DELAY_SECS,
	delayedAction: suspend ViewModel.() -> Unit = {},
	action: suspend ViewModel.() -> BasicResponse,
) {
	corout {
		state.processing()
		try {
			if (!action().isSucc()) throw Exception("Not successful response")
			state.success()
		} catch (e: Exception) {
			e.printStackTrace()
			state.error(e)
		}

		delay(delayTime)
		delayedAction()
		state.waiting()
	}
}
