package tr.qonferencer.trons.defaultLayouts

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.colors
import tr.qonferencer.trons.theme.defaultBorderSize

/**
 * Defaultly styled [HorizontalDivider]
 *
 * Horizontal line with
 * - color as [AppColors.element]
 * - thickness as [defaultBorderSize]
 */
@Composable
fun DefaultWideDivider() {
	HorizontalDivider(
		color = colors.text,
		thickness = defaultBorderSize,
	)
}
