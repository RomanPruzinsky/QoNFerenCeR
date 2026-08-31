package tr.qonferencer.screens.admin.customScreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.shared.dtos.CustomScreenAdminDto
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.translations.rawDynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.ConfirmDialog
import tr.qonferencer.trons.defaultLayouts.PADS_DEFAULT
import tr.qonferencer.trons.defaultLayouts.PADS_NONE
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.states.infoState.InfoStateLayout
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

private enum class EditorMode(
	val labelKey: String,
	val icon: ImageVector,
) {
	METADATA("admin.customScreen.editMetadata", Icons.Default.Description),
	LAYOUT("admin.customScreen.editLayout", Icons.Default.Dashboard),
}

@Composable
fun CustomScreenEditorScreen(screen: CustomScreenAdminDto) {
	val screensVM = viewModel<CustomScreensViewModel>()
	var mode by remember(screen.id) { mutableStateOf<EditorMode?>(null) }
	val showDeleteDialog = rememberFalse()

	BackHandler(enabled = mode != null) { mode = null }

	ConfirmDialog(
		opened = showDeleteDialog,
		message = dynamicTranslation("admin.customScreen.delete.confirm"),
		onConfirm = { screensVM.delete(screen.id) },
	)

	InfoStateLayout(stateFlow = screensVM.deleteState) {
		when (mode) {
			null -> Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.SpaceEvenly,
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				@Composable
				fun ActionButton(
					translationKey: String,
					icon: ImageVector,
					background: Color,
					onClick: () -> Unit,
				) {
					CardLayout(
						containerColor = background,
						innerPads = PADS_NONE,
						outerPads = PADS_DEFAULT,
					) {
						Row(
							modifier = Modifier
								.clickable(onClick = onClick)
								.defaultTextPadding()
								.defaultLayoutPadding(),
							horizontalArrangement = defaultSpacing,
							verticalAlignment = Alignment.CenterVertically,
						) {
							Text(
								text = dynamicTranslation(translationKey),
								style = typo.labelLarge,
							)
							Icon(
								imageVector = icon,
								contentDescription = rawDynamicTranslation(translationKey),
								tint = colors.text,
								modifier = Modifier.size(defaultIconSize),
							)
						}
					}
				}

				EditorMode.entries.forEach { option ->
					ActionButton(
						translationKey = option.labelKey,
						icon = option.icon,
						background = colors.clickable,
						onClick = { mode = option },
					)
				}

				ActionButton(
					translationKey = "admin.customScreen.delete",
					icon = Icons.Default.Delete,
					background = colors.action.delete,
					onClick = { showDeleteDialog.value = true },
				)
			}

			EditorMode.METADATA -> CustomScreenMetadataEditor(screen = screen)
			EditorMode.LAYOUT -> CustomScreenLayoutEditor(screen = screen)
		}
	}
}
