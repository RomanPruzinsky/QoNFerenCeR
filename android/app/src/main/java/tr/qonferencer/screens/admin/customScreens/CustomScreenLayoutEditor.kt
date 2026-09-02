package tr.qonferencer.screens.admin.customScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import tr.qonferencer.screens.customScreen.Render
import tr.qonferencer.shared.dtos.CustomElement
import tr.qonferencer.shared.dtos.CustomScreenAdminDto
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.DefaultWideDivider
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.TransparentGroupBox
import tr.qonferencer.trons.defaultLayouts.defaultHorizontalSpacing
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.defaultLayouts.defaultVerticalSpacing
import tr.qonferencer.trons.miscs.DEFAULT_STATE_CHANGE_DELAY_SECS
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.states.StateIndicator
import tr.qonferencer.trons.states.dataState.DataStateLayout
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.antsBorder
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding
import tr.qonferencer.trons.theme.halfDefaultLayoutPadding

@Composable
fun CustomScreenLayoutEditor(screen: CustomScreenAdminDto) {
	val screensVM = viewModel<CustomScreensViewModel>()

	var body by remember(screen.id) { mutableStateOf(screen.body) }
	var editing by remember(screen.id) { mutableStateOf(screen.body.isEmpty()) }
	var addTargetPath by remember { mutableStateOf<List<Int>?>(null) }
	var editTargetPath by remember { mutableStateOf<List<Int>?>(null) }

	fun applyEdit(newBody: List<CustomElement>) {
		body = newBody
	}

	DataStateLayout(
		stateFlow = screensVM.saveState,
		bodyOnWaiting = {
			Column(modifier = Modifier.fillMaxSize()) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.defaultLayoutPadding(),
					horizontalArrangement = Arrangement.spacedBy(halfDefaultLayoutPadding),
					verticalAlignment = Alignment.CenterVertically,
				) {
					@Composable
					fun CustomHeaderButton(
						icon: ImageVector,
						contentDescription: String,
						background: Color,
						onClick: () -> Unit,
					) {
						Icon(
							imageVector = icon,
							contentDescription = contentDescription,
							tint = colors.text,
							modifier = Modifier
								.defaultClip()
								.background(background)
								.clickable(onClick = onClick)
								.defaultTextPadding()
								.size(defaultIconSizeLarge),
						)
					}

					CustomHeaderButton(
						icon = if (editing) Icons.Default.Visibility else Icons.Default.Edit,
						contentDescription = "switch preview/edit mode",
						background = colors.clickable,
						onClick = { editing = !editing },
					)

					if (editing) {
						Spacer(Modifier.weight(1F))

						CustomHeaderButton(
							icon = Icons.Default.Close,
							contentDescription = "discard layout changes",
							background = colors.action.delete,
							onClick = { body = screen.body },
						)
						CustomHeaderButton(
							icon = Icons.Default.Check,
							contentDescription = "save layout changes",
							background = colors.action.approve,
							onClick = { screensVM.save(screen.copy(body = body)) },
						)
					}
				}

				DefaultWideDivider()

				ScrollableColumn(
					modifier = Modifier.fillMaxSize(),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = defaultSpacing,
				) {
					if (editing) {
						EditableElementListContent(
							elements = body,
							containerPath = emptyList(),
							onAdd = { addTargetPath = it },
							onEdit = { editTargetPath = it },
						)
					} else {
						body.forEach { it.Render() }
					}
				}
			}

			ElementPickerDialog(
				targetPath = addTargetPath,
				onDismiss = { addTargetPath = null },
				onAdd = { element ->
					val path = addTargetPath ?: return@ElementPickerDialog
					applyEdit(body.insertedAt(path, element))
					addTargetPath = null
				},
			)

			ElementEditDialog(
				element = editTargetPath?.let { body.elementAt(it) },
				onDismiss = { editTargetPath = null },
				onSave = { edited ->
					val path = editTargetPath ?: return@ElementEditDialog
					applyEdit(body.replacedAt(path, edited))
					editTargetPath = null
				},
				onDelete = {
					val path = editTargetPath ?: return@ElementEditDialog
					applyEdit(body.removedAt(path))
					editTargetPath = null
				},
			)
		},
		bodyOnSuccess = { saved ->
			StateIndicator(text = DefaultSay.SUCCESS)
			LaunchedEffect(saved) {
				delay(DEFAULT_STATE_CHANGE_DELAY_SECS)
				screensVM.resetSaveState()
			}
		},
	)
}

@Composable
fun EditableElementListContent(
	elements: List<CustomElement>,
	containerPath: List<Int>,
	onAdd: (List<Int>) -> Unit,
	onEdit: (List<Int>) -> Unit,
) {
	AddElementButton(onClick = { onAdd(containerPath + 0) })
	elements.forEachIndexed { index, element ->
		EditableElementNode(
			element = element,
			path = containerPath + index,
			onAdd = onAdd,
			onEdit = onEdit,
		)
	}
	if (elements.isNotEmpty()) AddElementButton(onClick = { onAdd(containerPath + elements.size) })
}

@Composable
private fun EditableElementNode(
	element: CustomElement,
	path: List<Int>,
	onAdd: (List<Int>) -> Unit,
	onEdit: (List<Int>) -> Unit,
) {
	when (element) {
		is CustomElement.Row -> TransparentGroupBox(
			indicatorText = dynamicTranslation("admin.customScreen.element.row"),
			modifier = Modifier.clickable { onEdit(path) },
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.horizontalScroll(rememberScrollState()),
				horizontalArrangement = defaultHorizontalSpacing(),
				verticalAlignment = Alignment.CenterVertically,
				content = { EditableElementListContent(element.children, path, onAdd, onEdit) },
			)
		}

		is CustomElement.Column -> TransparentGroupBox(
			indicatorText = dynamicTranslation("admin.customScreen.element.column"),
			modifier = Modifier.clickable { onEdit(path) },
		) {
			Column(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = defaultVerticalSpacing(),
				horizontalAlignment = Alignment.CenterHorizontally,
				content = { EditableElementListContent(element.children, path, onAdd, onEdit) },
			)
		}

		else -> Box(
			modifier = Modifier
				.defaultClip()
				.clickable { onEdit(path) },
			content = { element.Render() },
		)
	}
}

@Composable
private fun AddElementButton(onClick: () -> Unit) {
	Text(
		text = "+",
		style = typo.labelLarge,
		modifier = Modifier
			.defaultLayoutPadding()
			.defaultClip()
			.background(colors.clickable)
			.antsBorder()
			.clickable(onClick = onClick)
			.defaultLayoutPadding()
			.defaultLayoutPadding(Edge.HORIZONTAL),
	)
}
