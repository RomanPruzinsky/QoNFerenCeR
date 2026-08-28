package tr.qonferencer.trons.states

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.infoState.InfoState
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultTextPadding

/**
 * [Text] indicating current [InfoState] or [DataState]
 *
 * @param text Text to show
 * @param backgroundColor Background color of [text]
 * @param textStyle [TextStyle] of [text]
 */
@Composable
fun StateIndicator(text: String, backgroundColor: Color = colors.element, textStyle: TextStyle = typo.headlineMedium) {
	Text(
		text = text,
		style = textStyle,
		modifier = Modifier
			.defaultClip()
			.background(backgroundColor.takeOrElse { colors.navigation })
			.defaultTextPadding(),
	)
}
