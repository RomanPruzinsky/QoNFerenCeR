package tr.qonferencer.trons.screen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/** Used for android screen operations */
fun Context.findActivity(): Activity? {
	var context = this
	while (context is ContextWrapper) {
		if (context is Activity) return context
		context = context.baseContext
	}
	return null
}
