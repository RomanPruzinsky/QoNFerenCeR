package tr.qonferencer.trons.states.dataState

import tr.qonferencer.trons.states.StateBase

/** Different stages of data loading */
sealed class DataState<out T> : StateBase {
	data object Waiting : DataState<Nothing>(), StateBase.Waiting
	data object Processing : DataState<Nothing>(), StateBase.Processing
	data class Error(
		val specification: Exception? = null,
	) : DataState<Nothing>(),
		StateBase.Error
	data class Success<T>(
		val value: T,
	) : DataState<T>(),
		StateBase.Success
}
