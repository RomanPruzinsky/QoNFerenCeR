package tr.qonferencer.screens.admin.customScreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.navigation.iconFrom
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.translations.rawDynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.DialogFullWidth
import tr.qonferencer.trons.defaultLayouts.PADS_NONE
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.Toast
import tr.qonferencer.trons.miscs.cannotBeEmptyToast
import tr.qonferencer.trons.remembers.clear
import tr.qonferencer.trons.remembers.rememberEmptyString
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.states.dataState.DataStateLayout
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun CustomScreensPickerScreen() {
	val screensVM = viewModel<CustomScreensViewModel>()
	val selected by screensVM.selected.collectAsState()

	DisposableEffect(Unit) { onDispose { screensVM.deselect() } }

	if (selected != null) {
		BackHandler { screensVM.deselect() }
		CustomScreenEditorScreen(screen = selected!!)
	} else {
		DataStateLayout(
			stateFlow = screensVM.allScreensState,
		) { screens ->
			val showNewScreenDialog = rememberFalse()
			val newId = rememberEmptyString()
			val context = LocalContext.current

			ScrollableColumn(
				modifier = Modifier
					.fillMaxSize()
					.defaultLayoutPadding(),
				verticalArrangement = defaultSpacing,
			) {
				screens.forEach { screen ->
					CardLayout(
						modifier = Modifier.fillMaxWidth(),
						borderize = true,
						containerColor = colors.clickable,
						innerPads = PADS_NONE,
					) {
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.clickable { screensVM.select(screen) }
								.defaultLayoutPadding(),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically,
						) {
							Row(verticalAlignment = Alignment.CenterVertically) {
								Icon(
									imageVector = iconFrom(screen.icon),
									contentDescription = screen.id,
									tint = colors.text,
									modifier = Modifier.size(defaultIconSize),
								)
								Text(
									text = dynamicTranslation(screen.titleKey).ifBlank { screen.id },
									style = typo.labelLarge,
									modifier = Modifier.defaultTextPadding(),
								)
							}
							if (screen.isStartingScreen) {
								Icon(
									imageVector = Icons.Default.Home,
									contentDescription = "starting screen",
									tint = colors.level.leader,
									modifier = Modifier.size(defaultIconSize),
								)
							}
						}
					}
				}

				Text(
					text = " + ",
					style = typo.labelLarge,
					modifier = Modifier
						.align(Alignment.End)
						.defaultClip()
						.background(colors.action.approve)
						.clickable { showNewScreenDialog.value = true }
						.defaultTextPadding()
						.defaultLayoutPadding(),
				)
			}

			if (showNewScreenDialog.value) {
				DialogFullWidth(onDismissRequestAction = { showNewScreenDialog.value = false }) {
					Column(
						horizontalAlignment = Alignment.End,
						verticalArrangement = defaultSpacing,
					) {
						DefaultOTF(
							valueText = newId,
							labelText = dynamicTranslation("admin.customScreen.id"),
							modifier = Modifier.fillMaxWidth(),
						)

						Icon(
							imageVector = Icons.Default.Check,
							contentDescription = "create custom screen",
							tint = colors.text,
							modifier = Modifier
								.defaultLayoutPadding()
								.defaultClip()
								.background(colors.action.approve)
								.clickable {
									val trimmedId = newId.value.trim()
									if (trimmedId.isBlank()) {
										cannotBeEmptyToast(context)
										return@clickable
									}
									if (screens.any { it.id == trimmedId }) {
										Toast.short(context, rawDynamicTranslation("admin.customScreen.idTaken"))
										return@clickable
									}
									screensVM.create(trimmedId)
									showNewScreenDialog.value = false
									newId.clear()
								}
								.defaultTextPadding()
								.size(defaultIconSizeLarge),
						)
					}
				}
			}
		}
	}
}
