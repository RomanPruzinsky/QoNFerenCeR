package tr.qonferencer.trons.states.dataState

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.dataState.DataState.Success
import tr.qonferencer.trons.states.dataState.DataState.Waiting

/** Init [DataState] as [MutableStateFlow] to [Waiting] state */
fun <T> initDataState() = MutableStateFlow<DataState<T>>(Waiting)

/** Obtain value of [DataState] at [Success] */
fun <T> DataState<T>.successValue(): T = (this as Success<T>).value

/** Obtain value of [DataState] at [Success] */
fun <T> StateFlow<DataState<T>>.successValue(): T = this.value.successValue()

/** Keep obtaining value of [DataState] at [Success] */
@Composable
fun <T> StateFlow<DataState<T>>.gettingSuccessValue(): T = this.collectValue().successValue()

/** Sets [DataState] as [DataState.Waiting] */
fun <T> MutableStateFlow<DataState<T>>.waiting() {
	this.value = Waiting
}

/** Sets [DataState] as [DataState.Processing] */
fun <T> MutableStateFlow<DataState<T>>.processing() {
	this.value = DataState.Processing
}

/** Sets [DataState] as [DataState.Error] */
fun <T> MutableStateFlow<DataState<T>>.error(e: Exception) {
	this.value = DataState.Error(e)
}

/** Sets [DataState] as [DataState.Success] */
fun <T> MutableStateFlow<DataState<T>>.success(toSet: T) {
	this.value = Success(toSet)
}
