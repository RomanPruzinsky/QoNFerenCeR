package tr.qonferencer.screens.customScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.states.dataState.DataStateLayout

@Composable
fun CustomScreenLayout(id: String) {
	val screenVM = viewModel<CustomScreenViewModel>(key = id, factory = customScreenViewModelFactory(id))

	ScrollableColumn(
		modifier = Modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = defaultSpacing,
	) {
		DataStateLayout(stateFlow = screenVM.bodyState) { elements -> elements.forEach { it.Render() } }
	}
}
