package tr.qonferencer.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.theme.defaultLayoutPadding

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
	ScrollableColumn(
		modifier = modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
	) {
		//TODO: maybe program?
	}
}
