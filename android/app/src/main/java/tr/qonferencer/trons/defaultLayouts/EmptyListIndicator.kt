package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultTextPadding

private const val EMPTY_LIST_TEXT = "🤔⚠️🧐"

/**
 * Text saying [EMPTY_LIST_TEXT]
 * stylyzed similar to [CardLayout]
 *
 * @param backgroundColor Color of background
 * @param textStyle [TextStyle] of text
 */
@Composable
private fun EmptyListText(
	textStyle: TextStyle,
	backgroundColor: Color = colors.appBackground,
) {
	Text(
		text = EMPTY_LIST_TEXT,
		style = textStyle,
		modifier = Modifier
			.defaultClip()
			.background(backgroundColor)
			.defaultTextPadding(),
	)
}

/**
 * Text saying [EMPTY_LIST_TEXT]
 * stylyzed similar to [CardLayout]
 *
 * @param backgroundColor Color of background
 * @param textStyle [TextStyle] of text
 */
@Composable
fun EmptyListIndicator(
	backgroundColor: Color = colors.appBackground,
	textStyle: TextStyle = typo.displayMedium,
) {
	EmptyListText(textStyle, backgroundColor)
}

/**
 * Text saying [EMPTY_LIST_TEXT]
 * stylyzed similar to [CardLayout]
 * used as item in [LazyListScope]
 *
 * @param backgroundColor Color of background
 * @param textStyle [TextStyle] of text
 *
 * @see [EmptyListIndicator]
 */
fun LazyListScope.emptyListIndicator(
	backgroundColor: (@Composable () -> Color)? = null,
	textStyle: TextStyle? = null,
) {
	item {
		EmptyListIndicator(
			backgroundColor =
			if (backgroundColor != null) backgroundColor()
			else colors.appBackground,
			textStyle = textStyle ?: typo.displayMedium,
		)
	}
}
