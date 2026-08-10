package tr.qonferencer.trons.defaultLayouts

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultAnimation
import tr.qonferencer.trons.theme.defaultLayoutPadding

/** Don't apply spacing, use in [ActionedLazyColumn]/contentSpacing */
val NO_CONTENT_SPACING = 0.dp

/**
 * Display items in [LazyColumn]
 *
 * @param T Type of items in the list.
 * @param tList List of items to display.
 * @param tClick Function to be launched when item is clicked. If null, items won't be clickable.
 * @param key Key to identify items.
 * @param shouldSplit Bool indicating whether to display a [DefaultWideDivider] between items.
 * @param itemBackgoundColor Set background color of each item and [emptyListIndicator]. You can use argument on each item in the list specifically.
 * @param contentAlignment Horizontal alignment of each item.
 * @param textStyle [TextStyle] for [emptyListIndicator].
 * @param listState [LazyListState] to be used by [LazyColumn]. Defaults to new state created by [rememberLazyListState].
 * @param parentModifier [Modifier] to be applied to [LazyColumn] itself.
 * @param bodyModifier [Modifier] to be applied to each item's body.
 * @param contentPadding Bool indicating whether to apply [defaultLayoutPadding] on start and end of displayed list
 * @param contentSpacing Vertical spacing [Dp] between items in the list. Defaults to [defaultLayoutPadding]. Use [NO_CONTENT_SPACING] if no spacing is wanted.
 * @param element Item to display, receives index of item in list and item itself
 */
@Composable
fun <T> ActionedLazyColumn(
	tList: List<T>,
	tClick: ((T) -> Unit)?,
	@SuppressLint("ModifierParameter") parentModifier: Modifier = Modifier,
	bodyModifier: Modifier = Modifier,
	key: ((index: Int, item: T) -> Any)? = null,
	shouldSplit: Boolean = true,
	itemBackgoundColor: @Composable (T?) -> Color = { colors.navigation },
	contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
	textStyle: TextStyle = typo.displayMedium,
	listState: LazyListState = rememberLazyListState(),
	contentPadding: Boolean = false,
	contentSpacing: Dp = defaultLayoutPadding,
	element: @Composable (Int, T) -> Unit,
) {
	LazyColumn(
		modifier = parentModifier.defaultAnimation(),
		contentPadding = PaddingValues(vertical = if (contentPadding) defaultLayoutPadding else 0.dp),
		horizontalAlignment = contentAlignment,
		verticalArrangement = Arrangement.spacedBy(contentSpacing),
		state = listState,
	) {
		itemsIndexed(
			items = tList,
			key = key,
		) { index, tItem ->
			Box(
				modifier = bodyModifier
					.background(itemBackgoundColor(tItem))
					.then(
						if (tClick != null) Modifier.clickable { tClick(tItem) }
						else Modifier,
					)
					.defaultLayoutPadding(Edge.HORIZONTAL),
				contentAlignment = Alignment.Center,
			) {
				element(index, tItem)
			}
			
			if (shouldSplit && index < tList.lastIndex) {
				DefaultWideDivider()
			}
		}
		
		if (tList.isEmpty()) {
			emptyListIndicator(
				backgroundColor = { itemBackgoundColor(null) },
				textStyle = textStyle,
			)
		}
	}
}
