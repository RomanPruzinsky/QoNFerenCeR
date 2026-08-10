package tr.qonferencer.screens.customscreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.theme.defaultLayoutPadding

@Composable
fun CustomScreenScreen(id: String, modifier: Modifier = Modifier) {
	ScrollableColumn(
		modifier = modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
	) {
		//TODO: fetch and render body via CustomElement renderer
		Text("Custom screen: $id")
	}
}
