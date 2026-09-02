package tr.qonferencer.screens.admin.translations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import tr.qonferencer.shared.dtos.LanguageDto
import tr.qonferencer.shared.dtos.TranslationDto
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.translations.rawDynamicTranslation
import tr.qonferencer.trons.defaultLayouts.ApproveIconButton
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.DefaultHeightSpacer
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.DefaultWideDivider
import tr.qonferencer.trons.defaultLayouts.DeleteIconButton
import tr.qonferencer.trons.defaultLayouts.DialogFullWidth
import tr.qonferencer.trons.defaultLayouts.ProfileToggleRow
import tr.qonferencer.trons.miscs.EMPTY_STRING
import tr.qonferencer.trons.miscs.Toast
import tr.qonferencer.trons.miscs.cannotBeEmptyToast
import tr.qonferencer.trons.remembers.rememberEmptyString
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding
import tr.qonferencer.trons.theme.halfDefaultLayoutPadding

sealed class TranslationKeyDialogTarget {
	data object New : TranslationKeyDialogTarget()
	data class Existing(
		val key: String,
	) : TranslationKeyDialogTarget()
}

private sealed class LanguagesDialogMode {
	data object List : LanguagesDialogMode()
	data object Add : LanguagesDialogMode()
	data class Edit(
		val language: LanguageDto,
	) : LanguagesDialogMode()
}

@Composable
fun LanguagesDialog(
	show: Boolean,
	languages: List<LanguageDto>,
	onDismiss: () -> Unit,
	onAdd: (LanguageDto) -> Unit,
	onRename: (oldCode: String, newCode: String, newName: String, newIsDefault: Boolean) -> Unit,
	onDelete: (LanguageDto) -> Unit,
) {
	if (!show) return
	val mode = remember { mutableStateOf<LanguagesDialogMode>(LanguagesDialogMode.List) }

	DialogFullWidth(
		onDismissRequestAction = {
			if (mode.value is LanguagesDialogMode.List) onDismiss()
			else mode.value = LanguagesDialogMode.List
		},
	) {
		CardLayout {
			when (val current = mode.value) {
				LanguagesDialogMode.List -> {
					languages.forEach { lang ->
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.defaultClip()
								.clickable { mode.value = LanguagesDialogMode.Edit(lang) },
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically,
						) {
							Text(
								text = lang.name,
								style = typo.labelLarge,
								modifier = Modifier.defaultTextPadding(),
							)
							if (lang.isDefault) {
								Icon(
									imageVector = Icons.Default.Home,
									contentDescription = "default language",
									tint = colors.level.leader,
									modifier = Modifier.size(defaultIconSize),
								)
							}
						}
					}
					AddButton(label = " + ", onClick = { mode.value = LanguagesDialogMode.Add })
				}

				LanguagesDialogMode.Add -> AddLanguageForm(
					existingCodes = languages.map { it.code },
					onAdd = { newLang ->
						onAdd(newLang)
						mode.value = LanguagesDialogMode.List
					},
				)

				is LanguagesDialogMode.Edit -> EditLanguageForm(
					language = current.language,
					existingCodes = languages.map { it.code },
					onBack = { mode.value = LanguagesDialogMode.List },
					onRename = { newCode, newName, newIsDefault ->
						onRename(current.language.code, newCode, newName, newIsDefault)
						mode.value = LanguagesDialogMode.List
					},
				) {
					onDelete(current.language)
					mode.value = LanguagesDialogMode.List
				}
			}
		}
	}
}

@Composable
private fun ColumnScope.AddLanguageForm(
	existingCodes: List<String>,
	onAdd: (LanguageDto) -> Unit,
) {
	val context = LocalContext.current
	val keyboard = LocalSoftwareKeyboardController.current
	val focusManager = LocalFocusManager.current
	val code = rememberEmptyString()
	val name = rememberEmptyString()
	val nameFocusRequester = remember { FocusRequester() }

	ApproveIconButton(
		contentDescription = "add language",
		modifier = Modifier.align(Alignment.End),
		onClick = {
			val trimmedCode = code.value.trim()
			val trimmedName = name.value.trim()
			if (trimmedCode.isBlank() || trimmedName.isBlank()) {
				cannotBeEmptyToast(context)
				return@ApproveIconButton
			}
			if (existingCodes.any { it.equals(trimmedCode, ignoreCase = true) }) {
				Toast.short(context, rawDynamicTranslation("admin.translations.codeTaken"))
				return@ApproveIconButton
			}
			onAdd(LanguageDto(code = trimmedCode, name = trimmedName, isDefault = false))
		},
	)

	DefaultOTF(
		valueText = code,
		labelText = dynamicTranslation("admin.translations.langCode"),
		keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
		keyboardActions = KeyboardActions(onNext = { nameFocusRequester.requestFocus() }),
		modifier = Modifier
			.fillMaxWidth()
			.defaultLayoutPadding(),
	)
	DefaultOTF(
		valueText = name,
		labelText = dynamicTranslation("admin.translations.langName"),
		keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
		keyboardActions = KeyboardActions(
			onDone = {
				keyboard?.hide()
				focusManager.clearFocus(force = true)
			},
		),
		modifier = Modifier
			.fillMaxWidth()
			.defaultLayoutPadding()
			.focusRequester(nameFocusRequester),
	)
}

/**
 * Fields to rename, toggle default or delete [language]
 * @param existingCodes Already configured [LanguageDto.code]s, to reject duplicates on rename
 * @param onBack Returns to [LanguagesDialogMode.List]
 * @param onRename Called with new code, new name and new default flag on apply
 * @param onDelete Called on delete, blocked when [language] is default
 */
@Composable
private fun EditLanguageForm(
	language: LanguageDto,
	existingCodes: List<String>,
	onBack: () -> Unit,
	onRename: (newCode: String, newName: String, newIsDefault: Boolean) -> Unit,
	onDelete: () -> Unit,
) {
	val context = LocalContext.current
	val keyboard = LocalSoftwareKeyboardController.current
	val focusManager = LocalFocusManager.current
	val code = remember(language) { mutableStateOf(language.code) }
	val name = remember(language) { mutableStateOf(language.name) }
	val isDefault = remember(language) { mutableStateOf(language.isDefault) }
	val nameFocusRequester = remember { FocusRequester() }

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		HeaderIconButton(
			icon = Icons.Default.Close,
			contentDescription = "back to languages list",
			background = colors.clickable,
			onClick = onBack,
		)

		Row(horizontalArrangement = Arrangement.spacedBy(halfDefaultLayoutPadding)) {
			DeleteIconButton(
				contentDescription = "delete language ${language.code}",
				onClick = {
					if (language.isDefault) {
						Toast.short(context, rawDynamicTranslation("admin.translations.needDefaultLanguage"))
						return@DeleteIconButton
					}
					onDelete()
				},
			)
			ApproveIconButton(
				contentDescription = "rename language ${language.code}",
				onClick = {
					val trimmedCode = code.value.trim()
					val trimmedName = name.value.trim()
					if (trimmedCode.isBlank() || trimmedName.isBlank()) {
						cannotBeEmptyToast(context)
						return@ApproveIconButton
					}
					val taken = existingCodes.any { it != language.code && it.equals(trimmedCode, ignoreCase = true) }
					if (taken) {
						Toast.short(context, rawDynamicTranslation("admin.translations.codeTaken"))
						return@ApproveIconButton
					}
					onRename(trimmedCode, trimmedName, isDefault.value)
				},
			)
		}
	}

	DefaultHeightSpacer()
	DefaultWideDivider()
	DefaultHeightSpacer()
	ProfileToggleRow(dynamicTranslation("admin.translations.isDefault"), isDefault)

	DefaultOTF(
		valueText = code,
		labelText = dynamicTranslation("admin.translations.langCode"),
		keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
		keyboardActions = KeyboardActions(onNext = { nameFocusRequester.requestFocus() }),
		modifier = Modifier
			.fillMaxWidth()
			.defaultLayoutPadding(),
	)
	DefaultOTF(
		valueText = name,
		labelText = dynamicTranslation("admin.translations.langName"),
		keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
		keyboardActions = KeyboardActions(
			onDone = {
				keyboard?.hide()
				focusManager.clearFocus(force = true)
			},
		),
		modifier = Modifier
			.fillMaxWidth()
			.defaultLayoutPadding()
			.focusRequester(nameFocusRequester),
	)
}

@Composable
fun TranslationKeyDialog(
	target: TranslationKeyDialogTarget?,
	languages: List<LanguageDto>,
	translations: List<TranslationDto>,
	existingKeys: Set<String>,
	onDismiss: () -> Unit,
	onApply: (key: String, texts: Map<String, String>) -> Unit,
	onDelete: (key: String) -> Unit,
) {
	if (target == null) return
	val context = LocalContext.current
	val keyboard = LocalSoftwareKeyboardController.current
	val focusManager = LocalFocusManager.current

	fun hideKeyboard() {
		keyboard?.hide()
		focusManager.clearFocus(force = true)
	}

	val keyField = rememberEmptyString()
	val textFields = remember(target) {
		languages.associate { lang ->
			val initial =
				if (target is TranslationKeyDialogTarget.Existing) {
					translations.firstOrNull { it.key == target.key && it.langCode == lang.code }?.text.orEmpty()
				} else EMPTY_STRING
			lang.code to mutableStateOf(initial)
		}
	}

	DialogFullWidth(onDismissRequestAction = onDismiss) {
		CardLayout(
			modifier = Modifier
				.fillMaxWidth()
				.verticalScroll(rememberScrollState()),
			contentHorizontalAlignment = Alignment.CenterHorizontally,
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = when (target) {
					is TranslationKeyDialogTarget.Existing -> Arrangement.SpaceBetween
					TranslationKeyDialogTarget.New -> Arrangement.End
				},
			) {
				if (target is TranslationKeyDialogTarget.Existing) {
					DeleteIconButton(
						contentDescription = "delete translation key",
						onClick = {
							onDelete(target.key)
							onDismiss()
						},
					)
				}

				ApproveIconButton(
					contentDescription = "apply translation key",
					onClick = {
						val key = when (target) {
							is TranslationKeyDialogTarget.Existing -> target.key
							TranslationKeyDialogTarget.New -> keyField.value.trim()
						}
						if (key.isBlank()) {
							cannotBeEmptyToast(context)
							return@ApproveIconButton
						}
						if (target is TranslationKeyDialogTarget.New && key in existingKeys) {
							Toast.short(context, rawDynamicTranslation("admin.translations.keyTaken"))
							return@ApproveIconButton
						}
						onApply(key, textFields.mapValues { it.value.value.trim() })
						onDismiss()
					},
				)
			}

			when (target) {
				is TranslationKeyDialogTarget.Existing -> {
					Text(
						text = target.key,
						style = typo.headlineSmall,
						modifier = Modifier.defaultTextPadding(),
					)
				}

				TranslationKeyDialogTarget.New -> {
					DefaultOTF(
						valueText = keyField,
						labelText = dynamicTranslation("admin.translations.key"),
						keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
						keyboardActions = KeyboardActions(onDone = { hideKeyboard() }),
						specialCharFilter = { it != '\n' },
						modifier = Modifier
							.fillMaxWidth()
							.defaultLayoutPadding(),
					)
				}
			}

			DefaultWideDivider()

			languages.forEach { lang ->
				DefaultOTF(
					valueText = textFields.getValue(lang.code),
					labelText = lang.name,
					modifier = Modifier
						.fillMaxWidth()
						.defaultLayoutPadding(),
				)
			}
		}
	}
}
