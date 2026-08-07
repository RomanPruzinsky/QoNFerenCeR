package tr.qonferencer.trons.states.infoState

import kotlinx.coroutines.flow.MutableStateFlow
import tr.qonferencer.trons.states.infoState.InfoState.Waiting

/** Init [InfoState] as [MutableStateFlow] to [Waiting] state */
fun initInfoState() = MutableStateFlow<InfoState>(Waiting)

/** Sets [InfoState] as [InfoState.Waiting] */
fun MutableStateFlow<InfoState>.waiting() {
	this.value = InfoState.Waiting
}

/** Sets [InfoState] as [InfoState.Processing] */
fun MutableStateFlow<InfoState>.processing() {
	this.value = InfoState.Processing
}

/** Sets [InfoState] as [InfoState.Error] */
fun MutableStateFlow<InfoState>.error(e: Exception) {
	this.value = InfoState.Error(e)
}

/** Sets [InfoState] as [InfoState.Success] */
fun MutableStateFlow<InfoState>.success() {
	this.value = InfoState.Success
}
