package tr.qonferencer.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.data.remote.QoNFerenCeRApi
import tr.qonferencer.shared.dtos.SplashDto
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState

class SplashViewModel(
	private val splashApi: QoNFerenCeRApi.Splash,
) : ViewModel() {
	private val _splashState = initDataState<SplashDto>()
	val splashState = _splashState.asStateFlow()

	//TODO: not programmed by me
	init {
		load()
	}

	fun load() {
		dataStatedAction(_splashState) { splashApi.all() }
	}
}

internal fun splashViewModelFactory(splashApi: QoNFerenCeRApi.Splash) = viewModelFactory {
	initializer { SplashViewModel(splashApi) }
}
