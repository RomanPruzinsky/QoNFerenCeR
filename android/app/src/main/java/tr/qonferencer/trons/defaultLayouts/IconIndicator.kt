package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import tr.qonferencer.theme.colors
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.halfDefaultLayoutPadding
import tr.qonferencer.trons.theme.specPadding

/**
 * Box with [icon] in [alignment] corner
 *
 * @param modifier Modifier applied to whole element
 * @param shouldPaddingate Whether to apply [halfDefaultLayoutPadding] to [icon]
 * @param icon Icon to display
 * @param alignment Where to place [icon] within Box
 * @param tint Color of [icon]
 * @param content Content inside Box
 */
@Composable
fun IconIndicator(
	modifier: Modifier = Modifier,
	shouldPaddingate: Boolean = false,
	icon: ImageVector = Icons.Default.ContentCopy,
	alignment: Alignment = Alignment.TopEnd,
	tint: Color = colors.text,
	content: @Composable () -> Unit,
) {
	Box(modifier = modifier) {
		content()

		Icon(
			imageVector = icon,
			contentDescription = "Clickable action (usually copy)",
			tint = tint,
			modifier = Modifier
				.align(alignment)
				.specPadding(
					Edge.ALL to
						if (shouldPaddingate) halfDefaultLayoutPadding
						else 0.dp,
				),
		)
	}
}
