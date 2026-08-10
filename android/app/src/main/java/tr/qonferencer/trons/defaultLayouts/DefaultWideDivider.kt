package tr.qonferencer.trons.defaultLayouts

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.colors
import tr.qonferencer.trons.theme.defaultBorderSize

/**
 * Defaultly styled [HorizontalDivider]
 *
 * Horizontal line with
 * - color as [AppColors.element]
 * - thickness as [defaultBorderSize]
 *
 * @param modifier Optional [Modifier]
 */
@Composable
fun DefaultWideDivider(modifier: Modifier = Modifier) {
	HorizontalDivider(
		modifier = modifier,
		color = colors.element,
		thickness = defaultBorderSize,
	)
}
