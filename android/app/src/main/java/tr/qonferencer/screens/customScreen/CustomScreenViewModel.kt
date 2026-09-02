package tr.qonferencer.screens.customScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.shared.dtos.CustomElement
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState

class CustomScreenViewModel(
	id: String,
) : ViewModel() {
	private val _bodyState = initDataState<List<CustomElement>>()
	val bodyState = _bodyState.asStateFlow()

	init {
		dataStatedAction(_bodyState) { QoNFerenCerApi.customScreens.customScreenBody(id) }
	}
}

fun customScreenViewModelFactory(id: String) = viewModelFactory {
	initializer { CustomScreenViewModel(id) }
}
