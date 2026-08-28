package tr.qonferencer.screens.customScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.theme.defaultLayoutPadding

@Composable
fun CustomScreenLayout(id: String) {
	ScrollableColumn(
		modifier = Modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
	) {
		//TODO: fetch and render body via CustomElement renderer
		Text("Custom screen: $id", style = typo.bodyMedium)
	}
}
