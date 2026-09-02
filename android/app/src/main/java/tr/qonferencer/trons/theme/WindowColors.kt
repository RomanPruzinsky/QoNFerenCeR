package tr.qonferencer.trons.theme

import android.view.Window
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Set color of navigation (bottom) bar
 * @param color Color to set
 */
fun Window.setNavigationBarColor(color: Color) {
	this.navigationBarColor = color.toArgb()
}

/**
 * Set color of status (top) bar
 * @param color Color to set
 */
fun Window.setStatusBarColor(color: Color) {
	this.statusBarColor = color.toArgb()
}
