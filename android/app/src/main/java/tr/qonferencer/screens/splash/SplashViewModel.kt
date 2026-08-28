package tr.qonferencer.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.data.remote.QoNFerenCeRApi
import tr.qonferencer.shared.dtos.SplashDto
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState

class SplashViewModel(
	private val splashApi: QoNFerenCeRApi.Splash,
) : ViewModel() {
	private val _splashState = initDataState<SplashDto>()
	val splashState = _splashState.asStateFlow()

	init {
		load()
	}

	fun load() {
		dataStatedAction(_splashState) {
			splashApi.all().also {
				QoNFerenCeRApp.language.setNewData(it)
				QoNFerenCeRApp.currentUser.setDetails(it.me)
				QoNFerenCeRApp.customScreens.setScreens(it.customScreens)
				QoNFerenCeRApp.mealWindows.setWindows(it.mealWindows)
			}
		}
	}
}

fun splashViewModelFactory(splashApi: QoNFerenCeRApi.Splash) = viewModelFactory {
	initializer { SplashViewModel(splashApi) }
}
