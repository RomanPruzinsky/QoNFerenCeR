package tr.qonferencer.screens.admin.translations

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import tr.qonferencer.shared.dtos.LanguageDto
import tr.qonferencer.shared.dtos.TranslationDto
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.setClipboardTextWithNotification
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

/**
 * Recursively renders [nodes] as collapsible, `tree`-command-style list
 * @param expandeds [TranslationTreeNode.Category.path]s currently expanded
 * @param onToggle Toggles category's membership in [expandeds]
 * @param onEditKey Opens edit dialog for [TranslationTreeNode.Leaf.path]
 */
@Composable
fun TranslationTreeView(
	nodes: List<TranslationTreeNode>,
	languages: List<LanguageDto>,
	translations: List<TranslationDto>,
	expandeds: Set<String>,
	onToggle: (String) -> Unit,
	onEditKey: (String) -> Unit,
	depth: Int = 0,
) {
	Column(verticalArrangement = defaultSpacing) {
		nodes.forEach { node ->
			when (node) {
				is TranslationTreeNode.Category -> {
					val isExpanded = node.path in expandeds

					Row(
						modifier = Modifier
							.padding(start = offsetSize * depth)
							.fillMaxWidth()
							.defaultClip()
							.clickable { onToggle(node.path) }
							.defaultTextPadding(),
						horizontalArrangement = defaultSpacing,
						verticalAlignment = Alignment.CenterVertically,
					) {
						Icon(
							imageVector =
							if (isExpanded) Icons.Default.KeyboardArrowDown
							else Icons.AutoMirrored.Filled.KeyboardArrowRight,
							contentDescription =
							if (isExpanded) "collapse ${node.path}"
							else "expand ${node.path}",
							tint = colors.text,
							modifier = Modifier.size(defaultIconSize),
						)
						Text(
							text = "${".".repeat(depth)}${node.name} (${node.leafCount()})",
							style = typo.labelLarge,
						)
					}

					if (isExpanded) {
						TranslationTreeView(
							nodes = node.children,
							languages = languages,
							translations = translations,
							expandeds = expandeds,
							onToggle = onToggle,
							onEditKey = onEditKey,
							depth = depth + 1,
						)
					}
				}

				is TranslationTreeNode.Leaf -> TranslationLeafBox(
					node = node,
					languages = languages,
					translations = translations,
					depth = depth,
					onClick = { onEditKey(node.path) },
				)
			}
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TranslationLeafBox(
	node: TranslationTreeNode.Leaf,
	languages: List<LanguageDto>,
	translations: List<TranslationDto>,
	depth: Int,
	onClick: () -> Unit,
) {
	val clipboard = LocalClipboard.current
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	val copiedText = dynamicTranslation("misc.copied")

	Column(
		modifier = Modifier
			.padding(start = offsetSize * depth)
			.fillMaxWidth()
			.defaultClip()
			.background(colors.element)
			.combinedClickable(
				onClick = onClick,
				onLongClick = {
					setClipboardTextWithNotification(
						clipboardTitle = "QoNFerenCeR Translation Path",
						textToCopy = node.path,
						textIndicator = copiedText,
						clipboard = clipboard,
						context = context,
						scope = scope,
					)
				},
			)
			.defaultTextPadding(),
	) {
		Text(
			text = ".".repeat(depth) + node.name,
			style = typo.labelLarge,
		)

		languages.forEach { lang ->
			val text = translations.firstOrNull { it.key == node.path && it.langCode == lang.code }?.text.orEmpty()

			Row(verticalAlignment = Alignment.CenterVertically) {
				Text(
					text = "${lang.code.uppercase()}: ",
					style = typo.labelMedium,
					color = colors.action.hyperlinkText,
				)
				
				if (text.isBlank()) {
					Icon(
						imageVector = Icons.Default.Warning,
						contentDescription = dynamicTranslation("admin.translations.missing"),
						tint = colors.action.delete,
						modifier = Modifier.size(defaultIconSize),
					)
				} else {
					Text(
						text = text,
						style = typo.labelMedium,
					)
				}
			}
		}
	}
}

private val offsetSize = defaultLayoutPadding * 2
