package tr.qonferencer.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.api.auth.AuthRepository
import tr.qonferencer.api.parseLoginCredentials
import tr.qonferencer.trons.states.infoState.InfoState
import tr.qonferencer.trons.states.infoState.infoStatedAction

class LoginViewModel(
	private val authRepository: AuthRepository,
) : ViewModel() {
	private val _loginState = MutableStateFlow<InfoState>(InfoState.Waiting)
	val loginState = _loginState.asStateFlow()

	/** QR, NFC, and manual entry all resolve to this */
	fun submit(
		username: String,
		password: String,
	) {
		infoStatedAction(_loginState) { authRepository.login(username, password) }
	}

	/** Decodes a login-QR/NFC payload into username+password */
	fun submitScanned(payload: String) {
		val credentials = parseLoginCredentials(payload)
		if (credentials == null) {
			_loginState.value = InfoState.Error(IllegalArgumentException("Unreadable login QR"))
			return
		}
		submit(credentials.username, credentials.password)
	}
}

fun loginViewModelFactory(authRepository: AuthRepository) = viewModelFactory {
	initializer { LoginViewModel(authRepository) }
}
