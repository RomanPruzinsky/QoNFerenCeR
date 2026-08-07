package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
private fun EmptyListText(textStyle: TextStyle, backgroundColor: Color = MaterialTheme.colorScheme.background) {
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
	backgroundColor: Color = MaterialTheme.colorScheme.background,
	textStyle: TextStyle = MaterialTheme.typography.displayMedium,
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
			else MaterialTheme.colorScheme.background,
			textStyle = textStyle ?: MaterialTheme.typography.displayMedium,
		)
	}
}
