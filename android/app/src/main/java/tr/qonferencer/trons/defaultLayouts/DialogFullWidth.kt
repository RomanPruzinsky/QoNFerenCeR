package tr.qonferencer.trons.defaultLayouts

import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.rawClickable

/**
 * Full screen [Dialog]
 * @param onDismissRequestAction How to close dialog
 * @param contentAlignment Alignment of [body]
 * @param body What to show in [Dialog]
 */
@Composable
fun DialogFullWidth(
	onDismissRequestAction: () -> Unit,
	contentAlignment: Alignment = Alignment.Center,
	body: @Composable BoxScope.() -> Unit,
) {
	val view = LocalView.current
	val isKeyboardOpen = rememberFalse()

	var close: () -> Unit = {}

	Dialog(
		onDismissRequest = { close() },
		properties = DialogProperties(usePlatformDefaultWidth = false),
	) {
		/**	needs to be in Dialog, because it has its own LSKC */
		val keyboardController = LocalSoftwareKeyboardController.current

		/* detect if keyboard is open */
		DisposableEffect(view) {
			val viewTreeObserver = view.viewTreeObserver
			val listener = ViewTreeObserver.OnGlobalLayoutListener {
				isKeyboardOpen.value = ViewCompat.getRootWindowInsets(view)
					?.isVisible(WindowInsetsCompat.Type.ime()) != false
			}

			viewTreeObserver.addOnGlobalLayoutListener(listener)
			onDispose {
				viewTreeObserver.removeOnGlobalLayoutListener(listener)
			}
		}

		close = {
			if (isKeyboardOpen.value) {
				keyboardController?.hide()
			} else {
				onDismissRequestAction()
			}
		}

		Box(
			modifier = Modifier
				.fillMaxSize()
				.clickable { close() }
				.defaultLayoutPadding(multiplier = 2F),
			contentAlignment = contentAlignment,
		) {
			/* for when clicking on body, it doesn't detect .clickable in lower layered Box*/
			Box(modifier = Modifier.rawClickable { keyboardController?.hide() }) { body() }
		}

		BackHandler { close() }
	}
}
