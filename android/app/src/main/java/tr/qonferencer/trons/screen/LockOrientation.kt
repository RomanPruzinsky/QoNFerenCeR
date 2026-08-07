package tr.qonferencer.trons.screen

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/** Keep screen normal-oriented */
@Composable
fun LockPortraitOrientation() {
	LockOrientation(SCREEN_ORIENTATION_PORTRAIT)
}

/** Keep screen wide-oriented */
@Composable
fun LockLandscapeOrientation() {
	LockOrientation(SCREEN_ORIENTATION_LANDSCAPE)
}

/**
 * Lock screen orientation, eirher [SCREEN_ORIENTATION_PORTRAIT] or [SCREEN_ORIENTATION_LANDSCAPE]
 * @param wantedOrientation [SCREEN_ORIENTATION_PORTRAIT] or [SCREEN_ORIENTATION_LANDSCAPE]
 */
@SuppressLint("SourceLockedOrientationActivity")
@Composable
private fun LockOrientation(wantedOrientation: Int) {
	val context = LocalContext.current
	DisposableEffect(Unit) {
		val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
		val originalOrientation = activity.requestedOrientation
		activity.requestedOrientation = wantedOrientation
		onDispose { activity.requestedOrientation = originalOrientation }
	}
}
