package tr.qonferencer.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.screens.customscreen.CustomScreenLayout
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.theme.color
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultClipSize
import tr.qonferencer.trons.theme.specPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayout(modifier: Modifier = Modifier) {
	val startTarget = NavTarget.Fixed(QoNFerenCeRDestinations.startDest)
	var currentTarget by remember { mutableStateOf<NavTarget>(startTarget) }
	val coroutineScope = rememberCoroutineScope()
	val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

	val customScreens = QoNFerenCeRApp.customScreens.screens.collectValue()
	val currentRole = UserDetailDto.roleOrAnonym(QoNFerenCeRApp.currentUser.details.collectValue())

	BackHandler(enabled = currentTarget != startTarget) { currentTarget = startTarget }

	ModalNavigationDrawer(
		drawerContent = {
			ScreensMenuLayout(
				currentTarget = currentTarget,
				currentRole = currentRole,
				customScreens = customScreens,
				onSelect = { target ->
					currentTarget = target
					coroutineScope.launch { drawerState.close() }
				},
			)
		},
		drawerState = drawerState,
		modifier = modifier,
	) {
		Scaffold(
			contentWindowInsets = WindowInsets.systemBars,
			topBar = {
				TopAppBar(
					title = {
						Text(
							text = dynamicTranslation(currentTarget.titleKey),
							style = typo.headlineMedium,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
						)
					},
					colors = TopAppBarDefaults.topAppBarColors(containerColor = currentRole.color),
					navigationIcon = {
						IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
							Icon(Icons.Default.Menu, contentDescription = "navigation menu")
						}
					},
				)
			},
		) { paddingValues ->
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(colors.navigation),
			) {
				Box(
					modifier = Modifier
						.specPadding(Edge.TOP to paddingValues.calculateTopPadding())
						.fillMaxWidth()
						.height(defaultClipSize)
						.background(currentRole.color),
				)

				Box(
					modifier = Modifier
						.padding(paddingValues)
						.fillMaxSize()
						.defaultClip()
						.background(colors.appBackground),
				) {
					when (val target = currentTarget) {
						is NavTarget.Fixed -> target.destination.ProcessScreen()
						is NavTarget.Custom -> CustomScreenLayout(id = target.screen.id)
					}
				}
			}
		}
	}
}
