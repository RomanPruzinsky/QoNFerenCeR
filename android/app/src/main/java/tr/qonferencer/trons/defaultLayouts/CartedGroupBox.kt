package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import tr.qonferencer.trons.remembers.remember0dp
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultAnimation
import tr.qonferencer.trons.theme.defaultBorder
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultClipSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextHorizontalPadding
import tr.qonferencer.trons.theme.defaultTextVerticalPadding
import tr.qonferencer.trons.theme.specPadding

/**
 * [CardLayout] with indicator text
 *
 * @param modifier [Modifier] applied to parent
 * @param indicatorText String indicating data inside
 * @param textStyle [TextStyle] applied to [indicatorText]
 * @param backgroundColor [Color] that will be on background of [CardLayout] and [indicatorText]
 * @param content Body in [ColumnScope]
 *
 * @see [TransparentGroupBox]
 */
@Composable
fun CartedGroupBox(
	indicatorText: String,
	modifier: Modifier = Modifier,
	textStyle: TextStyle = MaterialTheme.typography.headlineSmall,
	backgroundColor: Color = MaterialTheme.colorScheme.surface,
	content: @Composable ColumnScope.() -> Unit,
) {
	val headerWidth = remember0dp()
	val localDensity = LocalDensity.current

	Box(
		modifier = modifier
			.defaultLayoutPadding()
			.background(backgroundColor, CircleShape.copy(CornerSize(defaultClipSize))),
	) {
		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier
				.defaultBorder()
				.defaultLayoutPadding()
				.defaultMinSize(minWidth = headerWidth.value + (defaultClipSize * 2)),
		) {
			Column(
				content = content,
				modifier = Modifier
					.defaultLayoutPadding(Edge.TOP)
					.defaultAnimation(),
				horizontalAlignment = Alignment.CenterHorizontally,
			)
		}

		Box(
			modifier = Modifier
				.offset(y = -defaultLayoutPadding)
				.specPadding(Edge.START to defaultClipSize),
		) {
			Text(
				text = indicatorText,
				style = textStyle,
				modifier = Modifier
					.defaultBorder()
					.defaultClip()
					.background(backgroundColor)
					.specPadding(
						Edge.TOP to defaultTextVerticalPadding / 2,
						Edge.START to defaultTextHorizontalPadding,
						Edge.END to defaultTextHorizontalPadding,
					)
					.onGloballyPositioned {
						headerWidth.value = with(localDensity) { it.size.width.toDp() }
					},
			)
		}
	}
}
