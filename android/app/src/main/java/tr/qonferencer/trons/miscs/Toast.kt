package tr.qonferencer.trons.miscs

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast as AndroidToast

/** Toast controller */
object Toast {
	val LENGTH_SHORT = AndroidToast.LENGTH_SHORT
	val LENGTH_LONG = AndroidToast.LENGTH_LONG

	/**
	 * SHORT (2s) [Toast] with text
	 * @param ctx Context to call
	 * @param text Showed text
	 */
	fun short(ctx: Context, text: String) = AndroidToast.makeText(ctx, text, LENGTH_SHORT).show()

	/**
	 * LONG (3.5s) [Toast] with text
	 * @param ctx Context to call
	 * @param text Showed text
	 */
	fun long(ctx: Context, text: String) = AndroidToast.makeText(ctx, text, LENGTH_LONG).show()
}

/**
 * Composable wrapper around [Toast.short] using [LocalContext.current].
 *
 * Fires the toast once when entering composition; re-fires whenever [key] changes.
 *
 * @param text Toast text
 * @param key Re-firing key. Defaults to [text] so changing the text re-shows the toast.
 */
@Composable
fun ShortToast(text: String, key: Any? = text) {
	val ctx = LocalContext.current
	LaunchedEffect(key) { Toast.short(ctx, text) }
}

/**
 * Composable wrapper around [Toast.long] using [LocalContext.current].
 *
 * Fires the toast once when entering composition; re-fires whenever [key] changes.
 *
 * @param text Toast text
 * @param key Re-firing key. Defaults to [text] so changing the text re-shows the toast.
 */
@Composable
fun LongToast(text: String, key: Any? = text) {
	val ctx = LocalContext.current
	LaunchedEffect(key) { Toast.long(ctx, text) }
}
