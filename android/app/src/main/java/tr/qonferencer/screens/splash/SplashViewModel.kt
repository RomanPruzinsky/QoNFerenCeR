package tr.qonferencer.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tr.qonferencer.data.remote.QoNFerenCeRApi
import tr.qonferencer.shared.dtos.SplashDto
import tr.qonferencer.state.DataState

class SplashViewModel(
	private val splashApi: QoNFerenCeRApi.Splash,
) : ViewModel() {
	private val _splashState = MutableStateFlow<DataState<SplashDto>>(DataState.Loading)
	val splashState = _splashState.asStateFlow()

	//TODO: not programmed by me
	init {
		load()
	}
	
	fun load() {
		viewModelScope.launch {
			_splashState.value = DataState.Loading
			_splashState.value = try {
				DataState.Success(splashApi.all())
			} catch (e: Exception) {
				DataState.Error(e)
			}
		}
	}
}

internal fun splashViewModelFactory(splashApi: QoNFerenCeRApi.Splash) = viewModelFactory {
	initializer { SplashViewModel(splashApi) }
}
