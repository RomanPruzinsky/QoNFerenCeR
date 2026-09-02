package tr.qonferencer.screens.mealScan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import tr.qonferencer.theme.colors
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.cannotBeEmptyToast
import tr.qonferencer.trons.remembers.rememberEmptyString
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding

/** Numeric user id entry, no name search */
@Composable
fun ManualMealIdEntry(onSubmit: (Long) -> Unit) {
	val idText = rememberEmptyString()
	val context = LocalContext.current
	val keyboard = LocalSoftwareKeyboardController.current
	val focusManager = LocalFocusManager.current
	
	fun submit() {
		val userId = idText.value.toLongOrNull()
		if (userId == null) {
			cannotBeEmptyToast(context)
			return
		}
		onSubmit(userId)
		keyboard?.hide()
		focusManager.clearFocus(force = true)
	}
	
	Row(
		modifier = Modifier.defaultLayoutPadding(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = defaultSpacing,
	) {
		DefaultOTF(
			valueText = idText,
			labelText = dynamicTranslation("user.detail.userId"),
			modifier = Modifier.weight(1F),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
			keyboardActions = KeyboardActions(onDone = { submit() }),
			specialCharFilter = { it.isDigit() },
		)
		
		Icon(
			imageVector = Icons.Default.Check,
			contentDescription = "submit",
			tint = colors.text,
			modifier = Modifier
				.defaultClip()
				.background(colors.clickable)
				.clickable { submit() }
				.defaultLayoutPadding()
				.size(defaultIconSizeLarge),
		)
	}
}
