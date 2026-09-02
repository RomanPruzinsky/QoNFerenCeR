package tr.qonferencer.trons.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

/** Locks host activity to portrait while composed, restoring previous value on dispose */
@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun LockPortraitOrientation() {
	val view = LocalView.current
	DisposableEffect(Unit) {
		val activity = view.context as Activity
		val original = activity.requestedOrientation
		activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
		onDispose { activity.requestedOrientation = original }
	}
}
