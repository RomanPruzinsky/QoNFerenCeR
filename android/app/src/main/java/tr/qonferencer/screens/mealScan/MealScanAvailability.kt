package tr.qonferencer.screens.mealScan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

/** Whether camera permission is granted, requesting it once if not */
@Composable
fun rememberHasCameraPermission(): Boolean {
	val context = LocalContext.current
	var hasPermission by remember { mutableStateOf(hasCameraPermission(context)) }
	val permissionLauncher =
		rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission = it }
	
	LaunchedEffect(Unit) { if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA) }
	LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { hasPermission = hasCameraPermission(context) }
	
	return hasPermission
}

/** Whether device has NFC hardware, currently enabled */
@Composable
fun rememberIsNfcAvailable(): Boolean {
	val context = LocalContext.current
	var isAvailable by remember { mutableStateOf(isNfcAvailable(context)) }
	
	LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { isAvailable = isNfcAvailable(context) }
	
	return isAvailable
}

private fun hasCameraPermission(context: Context) =
	ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

private fun isNfcAvailable(context: Context) = NfcAdapter.getDefaultAdapter(context)?.isEnabled == true
