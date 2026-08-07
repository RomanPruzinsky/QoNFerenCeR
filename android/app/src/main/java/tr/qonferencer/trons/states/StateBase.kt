package tr.qonferencer.trons.states

import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.infoState.InfoState

/**
 * Shared marker hierarchy for [DataState] and [InfoState].
 *
 * Lets generic state-check extensions (`isWaiting()`, `keepsError()`, …)
 * work uniformly over both state families.
 *
 * Not `sealed` so [DataState] / [InfoState] (in their own packages) can implement
 * the inner markers — Kotlin restricts sealed-type subclasses to the same package.
 */
interface StateBase {
	/** Marker for "no work has started" — see [DataState.Waiting] / [InfoState.Waiting] */
	interface Waiting : StateBase

	/** Marker for "work is in progress" — see [DataState.Processing] / [InfoState.Processing] */
	interface Processing : StateBase

	/** Marker for "work ended in failure" — see [DataState.Error] / [InfoState.Error] */
	interface Error : StateBase

	/** Marker for "work ended successfully" — see [DataState.Success] / [InfoState.Success] */
	interface Success : StateBase
}
