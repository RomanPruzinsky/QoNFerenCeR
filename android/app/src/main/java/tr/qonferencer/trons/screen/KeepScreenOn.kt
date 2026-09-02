package tr.qonferencer.trons.screen

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/** Don't lock or dim screen if composable where this is called is shown */
@Composable
fun KeepScreenOn() {
	val context = LocalContext.current
	DisposableEffect(Unit) {
		val window = context.findActivity()?.window
		window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
	}
}
