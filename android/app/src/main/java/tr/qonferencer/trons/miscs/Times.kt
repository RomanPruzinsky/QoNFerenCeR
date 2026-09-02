package tr.qonferencer.trons.miscs

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.infoState.InfoState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** Current [System] millis */
fun now(): Long = System.currentTimeMillis()

/** 1 second delay */
suspend fun delay1s() = delaySeconds(1)

/** Default delay for state change, used in [delayWaitingState] or with [DataState] or [InfoState] */
val DEFAULT_STATE_CHANGE_DELAY_SECS = 2.seconds

/** Delay [seconds] seconds */
suspend fun delaySeconds(seconds: Int) = delay(seconds * 1000L)

/**
 * Delay action for [InfoState] to change to [InfoState.Waiting]
 * @param state [InfoState] to change
 * @param delaySeconds How many seconds to wait for [InfoState] to change
 * @param refresh [InfoState] refresh action
 */
suspend fun delayWaitingState(
	state: MutableStateFlow<InfoState>,
	delaySeconds: Duration = DEFAULT_STATE_CHANGE_DELAY_SECS,
	refresh: () -> Unit = {},
) {
	delay(delaySeconds)
	state.value = InfoState.Waiting
	refresh()
}
