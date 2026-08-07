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
 * @param multiplier Multiplier of [defaultLayoutPadding]
 */
@Composable
fun DefaultWideSpacer(modifier: Modifier = Modifier, multiplier: Float = 1F) {
	Spacer(modifier.width(defaultLayoutPadding * multiplier))
}

/**
 * Horizontal spacer with [defaultLayoutPadding] * [multiplier]
 * ```
 * ...
 * SPACER
 * ...
 * ```
 * @param multiplier Multiplier of [defaultLayoutPadding]
 */
@Composable
fun DefaultHeightSpacer(modifier: Modifier = Modifier, multiplier: Float = 1F) {
	Spacer(modifier.height(defaultLayoutPadding * multiplier))
}
