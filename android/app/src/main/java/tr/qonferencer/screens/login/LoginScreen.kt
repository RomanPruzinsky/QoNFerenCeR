package tr.qonferencer.screens.login

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.api.authRepository
import tr.qonferencer.screens.splash.SplashViewModel
import tr.qonferencer.screens.splash.splashViewModelFactory
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.states.errorIndicatorMessage
import tr.qonferencer.trons.states.infoState.InfoState
import tr.qonferencer.trons.states.infoState.OnError
import tr.qonferencer.trons.states.infoState.OnProcessing
import tr.qonferencer.trons.theme.defaultLayoutPadding

@Composable
fun LoginScreen(
	modifier: Modifier = Modifier,
	viewModel: LoginViewModel = viewModel(factory = loginViewModelFactory(authRepository)),
	splashViewModel: SplashViewModel = viewModel(factory = splashViewModelFactory(QoNFerenCerApi.splash)),
) {
	val context = LocalContext.current
	var showManual by rememberSaveable { mutableStateOf(false) }
	var hasCameraPermission by remember {
		mutableStateOf(
			ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
		)
	}
	val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
		hasCameraPermission = granted
	}
	LaunchedEffect(Unit) {
		if (!hasCameraPermission && !showManual) permissionLauncher.launch(Manifest.permission.CAMERA)
	}

	// Tokens + mealSecret are already saved by here — relaunching splash is safe, re-fetches everything as if the app just started, already logged in
	val loginState by viewModel.loginState.collectAsState()
	LaunchedEffect(loginState) {
		if (loginState is InfoState.Success) splashViewModel.load()
	}

	ScrollableColumn(
		modifier = modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
		verticalArrangement = defaultSpacing,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(text = dynamicTranslation("login.title"), style = typo.headlineMedium)

		if (!showManual) {
			if (hasCameraPermission) {
				QrScannerView(
					onDecode = viewModel::submitScanned,
					modifier = Modifier
						.fillMaxWidth()
						.aspectRatio(1F),
				)
			} else {
				Text(text = dynamicTranslation("login.cameraDenied"))
				Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
					Text(text = dynamicTranslation("login.grantCamera"))
				}
			}
			TextButton(onClick = { showManual = true }) {
				Text(text = dynamicTranslation("login.useManual"))
			}
		} else {
			val username = remember { mutableStateOf("") }
			val password = remember { mutableStateOf("") }

			DefaultOTF(
				valueText = username,
				labelText = dynamicTranslation("login.username"),
				modifier = Modifier.fillMaxWidth(),
			)
			DefaultOTF(
				valueText = password,
				labelText = dynamicTranslation("login.password"),
				modifier = Modifier.fillMaxWidth(),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
			)
			Button(onClick = { viewModel.submit(username.value, password.value) }) {
				Text(text = dynamicTranslation("login.submit"))
			}
			TextButton(onClick = { showManual = false }) {
				Text(text = dynamicTranslation("login.useCamera"))
			}
		}

		viewModel.loginState.OnProcessing { CircularProgressIndicator() }
		viewModel.loginState.OnError { e -> Text(text = errorIndicatorMessage(e), color = colors.action.delete) }
	}
}
