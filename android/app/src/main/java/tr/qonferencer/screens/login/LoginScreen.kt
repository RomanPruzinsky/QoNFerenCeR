package tr.qonferencer.screens.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.api.authRepository
import tr.qonferencer.screens.splash.SplashViewModel
import tr.qonferencer.screens.splash.splashViewModelFactory
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.states.errorIndicatorMessage
import tr.qonferencer.trons.states.infoState.InfoState
import tr.qonferencer.trons.states.infoState.OnError
import tr.qonferencer.trons.states.infoState.OnProcessing
import tr.qonferencer.trons.theme.defaultLayoutPadding

enum class LoginMethod(
	val icon: ImageVector,
	val labelKey: String,
) {
	QR(Icons.Default.QrCode2, "login.by.qr"),
	NFC(Icons.Default.Nfc, "login.by.nfc"),
	MANUAL(Icons.Default.Keyboard, "login.by.manual"),
}

@Composable
fun LoginScreen() {
	val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory(authRepository))
	val splashViewModel: SplashViewModel = viewModel(factory = splashViewModelFactory(QoNFerenCerApi.splash))

	var selected by remember { mutableStateOf<LoginMethod?>(null) }

	val loginState by loginViewModel.loginState.collectAsState()
	LaunchedEffect(loginState) {
		when (loginState) {
			is InfoState.Success -> splashViewModel.load()
			is InfoState.Error -> selected = null
			else -> Unit
		}
	}

	BackHandler { selected = null }

	ScrollableColumn(
		modifier = Modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
		verticalArrangement = Arrangement.SpaceEvenly,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		when (selected) {
			null -> LoginMethodPicker(onSelect = { selected = it })
			LoginMethod.QR -> QrLoginScreen(onDecode = loginViewModel::submitScanned)
			LoginMethod.NFC -> NfcLoginScreen(onDecode = loginViewModel::submitScanned)
			LoginMethod.MANUAL -> ManualLoginScreen(onSubmit = loginViewModel::submit)
		}

		loginViewModel.loginState.OnProcessing { CircularProgressIndicator() }
		loginViewModel.loginState.OnError { e ->
			Text(
				text = errorIndicatorMessage(e),
				style = typo.bodyMedium,
				color = colors.action.delete,
			)
		}
	}
}
