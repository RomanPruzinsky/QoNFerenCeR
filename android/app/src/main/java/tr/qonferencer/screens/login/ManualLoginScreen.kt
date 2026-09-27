package tr.qonferencer.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.remembers.rememberEmptyString
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun ManualLoginScreen(onSubmit: (username: String, password: String) -> Unit) {
	val username = rememberEmptyString()
	val password = rememberEmptyString()

	val usernameFocusRequester = remember { FocusRequester() }
	val passwordFocusRequester = remember { FocusRequester() }

	val keyboard = LocalSoftwareKeyboardController.current
	val focusManager = LocalFocusManager.current

	fun submit() {
		onSubmit(username.value, password.value)
		keyboard?.hide()
		focusManager.clearFocus(force = true)
	}

	LaunchedEffect(Unit) { usernameFocusRequester.requestFocus() }

	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = defaultSpacing,
		horizontalAlignment = Alignment.End,
	) {
		DefaultOTF(
			valueText = username,
			labelText = dynamicTranslation("login.manual.username"),
			modifier = Modifier
				.fillMaxWidth()
				.focusRequester(usernameFocusRequester),
			keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
			keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
		)
		DefaultOTF(
			valueText = password,
			labelText = dynamicTranslation("login.manual.password"),
			modifier = Modifier
				.fillMaxWidth()
				.focusRequester(passwordFocusRequester),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
			keyboardActions = KeyboardActions(onDone = { submit() }),
		)
		Text(
			text = dynamicTranslation("login.manual.submit"),
			style = typo.labelLarge,
			modifier = Modifier
				.defaultClip()
				.clickable { submit() }
				.background(colors.clickable)
				.defaultTextPadding(),
		)
	}
}
