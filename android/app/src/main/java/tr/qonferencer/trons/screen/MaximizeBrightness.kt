package tr.qonferencer.trons.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/** Force screen brightness to max while shown */
@Composable
fun MaximizeBrightness() {
	val context = LocalContext.current

	DisposableEffect(Unit) {
		val window = context.findActivity()?.window ?: return@DisposableEffect onDispose {}
		val layoutParams = window.attributes
		val originalBrightness = layoutParams.screenBrightness

		layoutParams.screenBrightness = 1F
		window.attributes = layoutParams

		onDispose {
			layoutParams.screenBrightness = originalBrightness
			window.attributes = layoutParams
		}
	}
}
