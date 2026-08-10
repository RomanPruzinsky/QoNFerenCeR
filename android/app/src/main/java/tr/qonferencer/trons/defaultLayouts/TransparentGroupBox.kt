package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.remembers.remember0f
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultBorderSize
import tr.qonferencer.trons.theme.defaultClipSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.specPadding

/**
 * Rounded box with border and indicator text
 *
 * @param modifier [Modifier] applied to parent
 * @param indicatorText String indicating data inside
 * @param textStyle [TextStyle] applied to [indicatorText]
 * @param content Body in [ColumnScope]
 *
 * @see [CartedGroupBox]
 */
@Composable
fun TransparentGroupBox(
	indicatorText: String,
	modifier: Modifier = Modifier,
	textStyle: TextStyle = typo.headlineSmall,
	content: @Composable (ColumnScope.() -> Unit),
) {
	val density = LocalDensity.current
	val headerHeightPx = remember0f()
	val headerWidthPx = remember0f()
	val cornerRadiusPx = with(density) { defaultClipSize.toPx() }
	val borderWidthPx = with(density) { defaultBorderSize.toPx() }
	val headerWidthDp = with(density) { headerWidthPx.floatValue.toDp() }
	val borderColor = colors.element
	
	Box(
		modifier = modifier
			.specPadding(Edge.TOP to defaultLayoutPadding)
			.defaultLayoutPadding()
			.defaultMinSize(
				minWidth = defaultClipSize * 2 + headerWidthDp + defaultLayoutPadding,
				minHeight = defaultClipSize * 2,
			)
			.drawBehind {
				drawLine(
					borderColor,
					Offset(cornerRadiusPx + headerWidthPx.floatValue, 0f),
					Offset(size.width - cornerRadiusPx, 0f),
					borderWidthPx,
				)
				drawLine(
					borderColor,
					Offset(0f, cornerRadiusPx),
					Offset(0f, size.height - cornerRadiusPx),
					borderWidthPx,
				)
				drawLine(
					borderColor,
					Offset(size.width, cornerRadiusPx),
					Offset(size.width, size.height - cornerRadiusPx),
					borderWidthPx,
				)
				drawLine(
					borderColor,
					Offset(cornerRadiusPx, size.height),
					Offset(size.width - cornerRadiusPx, size.height),
					borderWidthPx,
				)
				
				drawArc(
					borderColor,
					180f,
					90f,
					false,
					Offset(0f, 0f),
					Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
					1f,
					Stroke(borderWidthPx),
				)
				drawArc(
					borderColor,
					270f,
					90f,
					false,
					Offset(size.width - cornerRadiusPx * 2, 0f),
					Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
					1f,
					Stroke(borderWidthPx),
				)
				drawArc(
					borderColor,
					90f,
					90f,
					false,
					Offset(0f, size.height - cornerRadiusPx * 2),
					Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
					1f,
					Stroke(borderWidthPx),
				)
				drawArc(
					borderColor,
					0f,
					90f,
					false,
					Offset(size.width - cornerRadiusPx * 2, size.height - cornerRadiusPx * 2),
					Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
					1f,
					Stroke(borderWidthPx),
				)
			},
	) {
		Box(
			modifier = Modifier.defaultLayoutPadding(),
			contentAlignment = Alignment.Center,
		) {
			Column(
				content = content,
				horizontalAlignment = Alignment.CenterHorizontally,
			)
		}
		
		Box(
			modifier = Modifier
				.offset(y = -(defaultLayoutPadding - defaultBorderSize))
				.specPadding(Edge.START to defaultClipSize)
				.onGloballyPositioned { lc ->
					headerWidthPx.floatValue = lc.size.width.toFloat()
					headerHeightPx.floatValue = lc.size.height.toFloat()
				},
		) {
			Text(
				text = indicatorText,
				style = textStyle,
				modifier = Modifier.specPadding(Edge.HORIZONTAL to defaultLayoutPadding),
			)
		}
	}
}
