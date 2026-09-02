package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tr.qonferencer.trons.theme.defaultLayoutPadding

/**
 * Vertical spacer with [defaultLayoutPadding] * [multiplier]
 * ```
 * ... SPACER ...
 * ```
 */
@Composable
fun DefaultWideSpacer(multiplier: Int = 1) {
	Spacer(Modifier.width(defaultLayoutPadding * multiplier))
}

/**
 * Horizontal spacer with [defaultLayoutPadding] * [multiplier]
 * ```
 * ...
 * SPACER
 * ...
 * ```
 */
@Composable
fun DefaultHeightSpacer(multiplier: Int = 1) {
	Spacer(Modifier.height(defaultLayoutPadding * multiplier))
}
