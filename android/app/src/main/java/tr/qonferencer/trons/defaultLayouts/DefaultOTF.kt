package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultOTFPadding

/**
 * Defaultly styled [OutlinedTextField] with some usefull text processing
 *
 * @param valueText [MutableState] of text shown
 * @param labelText Indicator text
 * @param textStyle [TextStyle] of [valueText] and [labelText]
 * @param modifier [Modifier] applied to parent
 * @param backgroundColor Color of whole element
 * @param keyboardOptions Keyboard specification, usually changing
 * - imeAction (for example: [androidx.compose.ui.text.input.ImeAction.Done])
 * - keyboardType (for example: [androidx.compose.ui.text.input.KeyboardType.Number])
 * @param keyboardActions Button actions, usually changing
 * - onDone (for example: onDone = { validate() })
 * @param bannees Banned characters
 * @param allowees Allowed characters
 * @param specialCharFilter Filter for every char, `true` to keep it
 * @param specialWholeFilter Filter for whole string, `true` to keep it
 * @param action Action to perform on value change, with params as `new` string and `original/last` string
 */
@Composable
fun DefaultOTF(
	valueText: MutableState<String>,
	labelText: String,
	modifier: Modifier = Modifier,
	textStyle: TextStyle = typo.labelMedium,
	backgroundColor: Color = Color.Unspecified,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	keyboardActions: KeyboardActions = KeyboardActions.Default,
	bannees: CharSequence? = null,
	allowees: CharSequence? = null,
	specialCharFilter: (Char) -> Boolean = { true },
	specialWholeFilter: (String) -> Boolean = { true },
	action: (new: String, original: String) -> Unit = { _, _ -> },
) {
	val usedBackgroundColor = backgroundColor.takeOrElse { colors.navigation }
	val otfColor = colors.selected

	OutlinedTextField(
		value = valueText.value,
		onValueChange = { newText ->
			val original = valueText.value
			if (newText.containsBanned(bannees) || !newText.allAllowed(allowees)) return@OutlinedTextField
			if (!specialWholeFilter(newText)) return@OutlinedTextField

			valueText.value = newText.filter { specialCharFilter(it) }
			action(newText, original)
		},
		label = { Text(text = labelText, style = textStyle) },
		textStyle = textStyle,
		colors = TextFieldDefaults.colors(
			cursorColor = otfColor,
			focusedIndicatorColor = otfColor,
			unfocusedIndicatorColor = otfColor,
			focusedLabelColor = otfColor,
			unfocusedLabelColor = otfColor,
			focusedContainerColor = usedBackgroundColor,
			unfocusedContainerColor = usedBackgroundColor,
		),
		keyboardOptions = keyboardOptions,
		keyboardActions = keyboardActions,
		modifier = modifier
			.defaultClip()
			.background(usedBackgroundColor)
			.defaultOTFPadding(),
	)
}

/**
 * Whether [this] string contains char from [bannees]
 * @receiver [String] to check for [bannees]
 * @param bannees Unwanted chars
 * @return `true` if [this] contains char from [bannees], `false` otherwise
 */
private fun String.containsBanned(bannees: CharSequence?): Boolean = this.any { bannees?.contains(it) ?: false }

/**
 * Whether [this] string has only chars from [allowees]
 * @receiver [String] to check for [allowees]
 * @param allowees Wanted chars
 * @return `true` if [this] has only chars from [allowees], `false` otherwise
 */
private fun String.allAllowed(allowees: CharSequence?): Boolean = this.all { allowees?.contains(it) ?: true }
