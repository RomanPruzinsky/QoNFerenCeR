package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * ## Column with modifiable [ScrollState]
 * See official [Column] documentation for more info
 *
 * @param modifier The modifier to be applied to the Column.
 * @param verticalArrangement The vertical arrangement of the layout's children.
 * @param horizontalAlignment The horizontal alignment of the layout's children.
 * @param content The content of the Column, with [ScrollState] as param
 * @see [Column]
 */
@Composable
fun ScrollableColumn(
	modifier: Modifier = Modifier,
	verticalArrangement: Arrangement.Vertical = Arrangement.Top,
	horizontalAlignment: Alignment.Horizontal = Alignment.Start,
	content: @Composable (ColumnScope.(ScrollState) -> Unit),
) {
	val scrollState = rememberScrollState()
	Column(
		modifier = modifier.verticalScroll(scrollState),
		verticalArrangement = verticalArrangement,
		horizontalAlignment = horizontalAlignment,
	) { content(scrollState) }
}
