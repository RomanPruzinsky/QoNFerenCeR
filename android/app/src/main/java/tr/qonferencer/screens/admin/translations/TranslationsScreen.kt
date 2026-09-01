package tr.qonferencer.screens.admin.translations

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
import androidx.compose.material.icons.filled.Close
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
import tr.qonferencer.shared.dtos.AllTranslationsDto
import tr.qonferencer.shared.dtos.TranslationDto
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.defaultLayouts.DefaultWideDivider
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.DEFAULT_STATE_CHANGE_DELAY_SECS
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.miscs.EMPTY_STRING
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.states.StateIndicator
import tr.qonferencer.trons.states.dataState.DataStateLayout
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding
import tr.qonferencer.trons.theme.halfDefaultLayoutPadding

@Composable
fun TranslationsScreen() {
	val translationsVM = viewModel<TranslationsViewModel>()

	DataStateLayout(stateFlow = translationsVM.allState) { original ->
		var languages by remember { mutableStateOf(original.languages) }
		var translations by remember { mutableStateOf(original.translations) }
		val isChanged = languages != original.languages || translations != original.translations

		var expandeds by remember { mutableStateOf(setOf<String>()) }

		val showLanguages = rememberFalse()
		var showKeyDialog by remember { mutableStateOf<TranslationKeyDialogTarget?>(null) }

		DataStateLayout(
			stateFlow = translationsVM.saveState,
			bodyOnWaiting = {
				Column(
					modifier = Modifier.fillMaxSize(),
					verticalArrangement = defaultSpacing,
				) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.defaultLayoutPadding(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically,
					) {
						Row(horizontalArrangement = Arrangement.spacedBy(halfDefaultLayoutPadding)) {
							AddButton(
								label = "🌐",
								background = colors.clickable,
								onClick = { showLanguages.value = true },
							)
							AddButton(label = "+ 🔑", onClick = { showKeyDialog = TranslationKeyDialogTarget.New })
						}

						if (isChanged) {
							Row(horizontalArrangement = Arrangement.spacedBy(halfDefaultLayoutPadding)) {
								HeaderIconButton(
									icon = Icons.Default.Close,
									contentDescription = "discard translation changes",
									background = colors.action.delete,
									onClick = {
										languages = original.languages
										translations = original.translations
									},
								)
								HeaderIconButton(
									icon = Icons.Default.Check,
									contentDescription = "save translation changes",
									background = colors.action.approve,
									onClick = { translationsVM.save(AllTranslationsDto(languages, translations)) },
								)
							}
						}
					}

					DefaultWideDivider()

					ScrollableColumn(
						modifier = Modifier
							.fillMaxSize()
							.defaultLayoutPadding(),
						verticalArrangement = defaultSpacing,
					) {
						TranslationTreeView(
							nodes = buildTranslationTree(translations.map { it.key }.distinct()),
							languages = languages,
							translations = translations,
							expandeds = expandeds,
							onToggle = { path ->
								expandeds =
									if (path in expandeds) expandeds - path
									else expandeds + path
							},
							onEditKey = { key -> showKeyDialog = TranslationKeyDialogTarget.Existing(key) },
						)
					}
				}

				LanguagesDialog(
					show = showLanguages.value,
					languages = languages,
					onDismiss = { showLanguages.value = false },
					onAdd = { newLang ->
						languages = languages + newLang
						val existingKeys = translations.map { it.key }.distinct()
						translations =
							translations + existingKeys.map { key -> TranslationDto(key, newLang.code, EMPTY_STRING) }
					},
					onRename = { oldCode, newCode, newName ->
						languages = languages.map {
							if (it.code == oldCode) it.copy(code = newCode, name = newName)
							else it
						}
						if (newCode != oldCode) {
							translations = translations.map {
								if (it.langCode == oldCode) it.copy(langCode = newCode)
								else it
							}
						}
					},
					onDelete = { lang ->
						languages = languages.filterNot { it.code == lang.code }
						translations = translations.filterNot { it.langCode == lang.code }
					},
				)

				TranslationKeyDialog(
					target = showKeyDialog,
					languages = languages,
					translations = translations,
					existingKeys = translations.map { it.key }.toSet(),
					onDismiss = { showKeyDialog = null },
					onApply = { key, texts ->
						translations = translations.filterNot { it.key == key } +
							texts.map { (langCode, text) -> TranslationDto(key, langCode, text) }
					},
					onDelete = { key -> translations = translations.filterNot { it.key == key } },
				)
			},
			bodyOnSuccess = { saved ->
				StateIndicator(text = DefaultSay.SUCCESS)
				LaunchedEffect(saved) {
					delay(DEFAULT_STATE_CHANGE_DELAY_SECS)
					translationsVM.resetSaveState()
				}
			},
		)
	}
}

@Composable
fun AddButton(
	label: String,
	background: Color = colors.action.approve,
	onClick: () -> Unit,
) {
	Text(
		text = label,
		style = typo.labelLarge,
		modifier = Modifier
			.defaultClip()
			.background(background)
			.clickable(onClick = onClick)
			.defaultTextPadding(),
	)
}

@Composable
fun HeaderIconButton(
	icon: ImageVector,
	contentDescription: String,
	background: Color,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	Icon(
		imageVector = icon,
		contentDescription = contentDescription,
		tint = colors.text,
		modifier = modifier
			.defaultClip()
			.background(background)
			.clickable(onClick = onClick)
			.defaultTextPadding()
			.size(defaultIconSizeLarge),
	)
}
