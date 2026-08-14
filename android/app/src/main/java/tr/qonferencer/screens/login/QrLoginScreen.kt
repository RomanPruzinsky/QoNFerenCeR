package tr.qonferencer.screens.login

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun QrLoginScreen(onDecode: (String) -> Unit) {
	val context = LocalContext.current

	var hasCameraPermission by remember { mutableStateOf(currentlyGranted(context)) }
	val permissionLauncher =
		rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasCameraPermission = it }

	LaunchedEffect(Unit) { if (!hasCameraPermission) permissionLauncher.askForCamera() }

	val lifecycleOwner = LocalLifecycleOwner.current
	DisposableEffect(lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) hasCameraPermission = currentlyGranted(context)
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
	}

	if (hasCameraPermission) {
		QrScannerView(onDecode = onDecode, modifier = Modifier.fillMaxSize())
	} else {
		Text(
			text = dynamicTranslation("login.state.cameraDenied"),
			style = typo.bodyLarge,
		)
		Text(
			text = dynamicTranslation("login.state.grantCamera"),
			style = typo.labelLarge,
			modifier = Modifier
				.defaultClip()
				.background(colors.clickable)
				.clickable { permissionLauncher.askForCamera() }
				.defaultTextPadding(),
		)
	}
}

private fun ManagedActivityResultLauncher<String, Boolean>.askForCamera() = this.launch(Manifest.permission.CAMERA)

private fun currentlyGranted(context: Context) =
	ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
