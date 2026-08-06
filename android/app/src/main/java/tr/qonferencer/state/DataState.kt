package tr.qonferencer.state

sealed class DataState<out T> {
	data object Loading : DataState<Nothing>()
	data class Error(
		val cause: Exception,
	) : DataState<Nothing>()
	data class Success<T>(
		val value: T,
	) : DataState<T>()
}
