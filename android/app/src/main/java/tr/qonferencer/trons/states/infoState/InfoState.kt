package tr.qonferencer.trons.states.infoState

import tr.qonferencer.trons.states.StateBase

/** Different stages of informing of data processing */
sealed class InfoState : StateBase {
	data object Waiting : InfoState(), StateBase.Waiting
	data object Processing : InfoState(), StateBase.Processing
	data class Error(
		val specification: Exception? = null,
	) : InfoState(),
		StateBase.Error
	data object Success : InfoState(), StateBase.Success
}
