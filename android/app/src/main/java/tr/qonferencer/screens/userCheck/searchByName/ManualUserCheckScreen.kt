package tr.qonferencer.screens.userCheck.searchByName

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.theme.colors
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.cannotBeEmptyToast
import tr.qonferencer.trons.remembers.rememberEmptyString
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.dataState.DataStateLayout
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding

@Composable
fun ManualUserCheckScreen(onPick: (Long) -> Unit) {
	val searchVM = viewModel<UserSearchByNameViewModel>()
	
	DataStateLayout(
		stateFlow = searchVM.searchState,
		bodyOnWaiting = {
			val query = rememberEmptyString()
			val context = LocalContext.current
			val keyboard = LocalSoftwareKeyboardController.current
			val focusManager = LocalFocusManager.current
			
			fun submitSearch() {
				if (query.value.isEmpty()) {
					cannotBeEmptyToast(context)
					return
				}
				searchVM.searchByName(query.value)
				keyboard?.hide()
				focusManager.clearFocus(force = true)
			}
			
			Column(
				modifier = Modifier.defaultLayoutPadding(),
				horizontalAlignment = Alignment.End,
				verticalArrangement = defaultSpacing,
			) {
				DefaultOTF(
					valueText = query,
					labelText = dynamicTranslation("userCheck.manual.searchLabel"),
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
					keyboardActions = KeyboardActions(onSearch = { submitSearch() }),
				)
				
				Icon(
					imageVector = Icons.Default.Search,
					contentDescription = "Search",
					tint = colors.text,
					modifier = Modifier
						.defaultLayoutPadding()
						.defaultClip()
						.background(colors.clickable)
						.clickable { submitSearch() }
						.defaultLayoutPadding()
						.size(defaultIconSizeLarge),
				)
			}
		},
	) { page ->
		BackHandler { searchVM.resetSearch() }
		UserSearchResultsScreen(
			page = page,
			loadingMore = searchVM.loadingMore.collectValue(),
			onPick = onPick,
			onLoadMore = searchVM::loadNextPage,
		)
	}
}
