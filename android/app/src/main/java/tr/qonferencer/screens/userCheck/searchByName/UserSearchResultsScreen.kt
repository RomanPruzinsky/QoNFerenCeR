package tr.qonferencer.screens.userCheck.searchByName

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import tr.qonferencer.shared.dtos.PageDto
import tr.qonferencer.shared.dtos.UserDisplayDto
import tr.qonferencer.theme.color
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.defaultLayouts.ActionedLazyColumn
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.PADS_NONE
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding

private const val LOAD_MORE_THRESHOLD = 5

@Composable
fun UserSearchResultsScreen(
	page: PageDto<UserDisplayDto>,
	loadingMore: Boolean,
	onPick: (Long) -> Unit,
	onLoadMore: () -> Unit,
) {
	val listState = rememberLazyListState()
	val shouldLoadMore by remember(page) {
		derivedStateOf {
			val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
			lastVisible >= page.content.lastIndex - LOAD_MORE_THRESHOLD
		}
	}
	val currentOnLoadMore by rememberUpdatedState(onLoadMore)
	LaunchedEffect(shouldLoadMore, page) { if (shouldLoadMore) currentOnLoadMore() }
	
	Box(modifier = Modifier.fillMaxSize()) {
		ActionedLazyColumn(
			tList = page.content,
			tClick = null,
			listState = listState,
			shouldSplit = false,
			contentPadding = true,
			itemBackgoundColor = { Color.Transparent },
			parentModifier = Modifier.fillMaxSize(),
		) { _, user ->
			CardLayout(
				modifier = Modifier.fillMaxWidth(),
				outerPads = PADS_NONE,
				innerPads = PADS_NONE,
				containerColor = user.role.color,
			) {
				Text(
					text = user.fullName,
					style = typo.bodyLarge,
					modifier = Modifier
						.fillMaxWidth()
						.clickable { onPick(user.userId) }
						.defaultLayoutPadding(),
				)
			}
		}
		
		if (loadingMore) {
			CircularProgressIndicator(
				color = colors.text,
				modifier = Modifier
					.size(defaultIconSizeLarge)
					.align(Alignment.BottomEnd)
					.defaultLayoutPadding(),
			)
		}
	}
}
