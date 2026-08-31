package tr.qonferencer.screens.admin.customScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import tr.qonferencer.shared.dtos.CustomElement
import tr.qonferencer.shared.dtos.TextSource
import tr.qonferencer.shared.enums.CustomTextSize
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.DialogFullWidth
import tr.qonferencer.trons.defaultLayouts.defaultHorizontalSpacing
import tr.qonferencer.trons.miscs.EMPTY_STRING
import tr.qonferencer.trons.remembers.rememberString
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding
import tr.qonferencer.trons.theme.halfDefaultLayoutPadding
import tr.qonferencer.trons.theme.orTransparentIf

private enum class ElementKind(
	val labelKey: String,
	val icon: ImageVector,
) {
	TEXT("admin.customScreen.element.text", Icons.Default.TextFields),
	IMAGE("admin.customScreen.element.image", Icons.Default.Image),
	ROW("admin.customScreen.element.row", Icons.Default.ViewColumn),
	COLUMN("admin.customScreen.element.column", Icons.Default.TableRows),
}

@Composable
private fun ElementKindLabel(
	kind: ElementKind,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.spacedBy(halfDefaultLayoutPadding),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Icon(
			imageVector = kind.icon,
			contentDescription = kind.labelKey,
			tint = colors.text,
			modifier = Modifier.size(defaultIconSize),
		)
		Text(text = dynamicTranslation(kind.labelKey), style = typo.labelLarge)
	}
}

@Composable
fun ElementPickerDialog(
	targetPath: List<Int>?,
	onDismiss: () -> Unit,
	onAdd: (CustomElement) -> Unit,
) {
	if (targetPath == null) return
	var kind by remember(targetPath) { mutableStateOf<ElementKind?>(null) }

	DialogFullWidth(onDismissRequestAction = onDismiss) {
		CardLayout(
			contentHorizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.width(IntrinsicSize.Max)
				.verticalScroll(rememberScrollState()),
		) {
			when (kind) {
				null -> ElementKind.entries.forEach { option ->
					ElementKindLabel(
						kind = option,
						modifier = Modifier
							.fillMaxWidth()
							.defaultClip()
							.clickable {
								when (option) {
									ElementKind.ROW -> onAdd(CustomElement.Row(emptyList()))
									ElementKind.COLUMN -> onAdd(CustomElement.Column(emptyList()))
									else -> kind = option
								}
							}
							.defaultTextPadding(),
					)
				}

				ElementKind.TEXT -> TextElementForm(onConfirm = { source, size -> onAdd(CustomElement.Text(source, size)) })
				ElementKind.IMAGE -> ImageElementForm(onConfirm = { url -> onAdd(CustomElement.Image(url)) })
				else -> Unit
			}
		}
	}
}

@Composable
fun ElementEditDialog(
	element: CustomElement?,
	onDismiss: () -> Unit,
	onSave: (CustomElement) -> Unit,
	onDelete: () -> Unit,
) {
	if (element == null) return

	DialogFullWidth(onDismissRequestAction = onDismiss) {
		CardLayout(
			modifier = Modifier
				.fillMaxWidth()
				.verticalScroll(rememberScrollState()),
			contentHorizontalAlignment = Alignment.CenterHorizontally,
		) {
			when (element) {
				is CustomElement.Text -> TextElementForm(
					initial = element,
					onConfirm = { source, size -> onSave(element.copy(source = source, size = size)) },
				)

				is CustomElement.Image -> ImageElementForm(
					initial = element,
					onConfirm = { url -> onSave(element.copy(url = url)) },
				)

				is CustomElement.Row -> ElementKindLabel(ElementKind.ROW)
				is CustomElement.Column -> ElementKindLabel(ElementKind.COLUMN)
			}

			Icon(
				imageVector = Icons.Default.Delete,
				contentDescription = "delete element",
				tint = colors.text,
				modifier = Modifier
					.align(Alignment.End)
					.defaultLayoutPadding()
					.defaultClip()
					.background(colors.action.delete)
					.clickable(onClick = onDelete)
					.defaultTextPadding()
					.size(defaultIconSizeLarge),
			)
		}
	}
}

@Composable
private fun ColumnScope.TextElementForm(
	initial: CustomElement.Text? = null,
	onConfirm: (TextSource, CustomTextSize) -> Unit,
) {
	val isLink = remember { mutableStateOf(initial?.source is TextSource.Link) }
	val value = rememberString(
		when (val source = initial?.source) {
			is TextSource.Ref -> source.key
			is TextSource.Link -> source.url
			null -> EMPTY_STRING
		},
	)
	var size by remember { mutableStateOf(initial?.size ?: CustomTextSize.MEDIUM) }

	Row(horizontalArrangement = defaultHorizontalSpacing()) {
		Text(
			text = dynamicTranslation("admin.customScreen.text.ref"),
			style = typo.labelMedium,
			modifier = Modifier
				.defaultClip()
				.background(colors.clickable.orTransparentIf(isLink.value))
				.clickable { isLink.value = false }
				.defaultTextPadding(),
		)
		Text(
			text = dynamicTranslation("admin.customScreen.text.link"),
			style = typo.labelLarge,
			modifier = Modifier
				.defaultClip()
				.background(colors.clickable.orTransparentIf(!isLink.value))
				.clickable { isLink.value = true }
				.defaultTextPadding(),
		)
	}

	DefaultOTF(
		valueText = value,
		labelText = dynamicTranslation(if (isLink.value) "admin.customScreen.text.url" else "admin.customScreen.text.key"),
		modifier = Modifier
			.fillMaxWidth()
			.defaultLayoutPadding(),
	)

	FlowRow(horizontalArrangement = defaultHorizontalSpacing()) {
		CustomTextSize.entries.forEach { option ->
			Text(
				text = dynamicTranslation(option.labelKey),
				style = typo.labelMedium,
				modifier = Modifier
					.defaultClip()
					.background(colors.clickable.orTransparentIf(size != option))
					.clickable { size = option }
					.defaultTextPadding(),
			)
		}
	}

	ApproveButton {
		if (value.value.isBlank()) return@ApproveButton
		val source =
			if (isLink.value) TextSource.Link(value.value.trim())
			else TextSource.Ref(value.value.trim())
		onConfirm(source, size)
	}
}

@Composable
private fun ColumnScope.ImageElementForm(
	initial: CustomElement.Image? = null,
	onConfirm: (String) -> Unit,
) {
	val url = rememberString(initial?.url ?: EMPTY_STRING)

	DefaultOTF(
		valueText = url,
		labelText = dynamicTranslation("admin.customScreen.image.url"),
		modifier = Modifier.fillMaxWidth(),
	)

	ApproveButton {
		if (url.value.isBlank()) return@ApproveButton
		onConfirm(url.value.trim())
	}
}

@Composable
private fun ColumnScope.ApproveButton(onClick: () -> Unit) {
	Icon(
		imageVector = Icons.Default.Check,
		contentDescription = "approve",
		tint = colors.text,
		modifier = Modifier
			.defaultLayoutPadding()
			.align(Alignment.End)
			.defaultClip()
			.background(colors.action.approve)
			.clickable(onClick = onClick)
			.defaultTextPadding()
			.size(defaultIconSizeLarge),
	)
}
