package tr.qonferencer.trons.miscs

import android.content.ClipData
import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Copies text to clipboard and notifies user
 *
 * @param clipboardTitle Indicator in clipboard
 * @param textToCopy Text to save to clipboard
 * @param textIndicator Text displalyed on [Toast.short]
 * @param clipboard [LocalClipboard]
 * @param context [LocalContext]
 * @param scope [rememberCoroutineScope] or any other scope
 */
fun setClipboardTextWithNotification(
	clipboardTitle: String,
	textToCopy: String,
	textIndicator: String,
	clipboard: Clipboard,
	context: Context,
	scope: CoroutineScope,
) {
	scope.launch {
		val clipData = ClipData.newPlainText(clipboardTitle, textToCopy)
		clipboard.setClipEntry(clipData.toClipEntry())
		Toast.short(context, textIndicator)
	}
}
