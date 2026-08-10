package tr.qonferencer.trons.defaultLayouts

import android.annotation.SuppressLint
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.colors
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultLayoutPadding

/**
 * Default [Switch] with customizable colors.
 *
 * @param modifier Applied [Modifier].
 * @param checked Boolean representing if [Switch] on/off
 * @param colorOn [Color] used for indicator and track when [Switch] is checked.
 * @param colorOff [Color] used for indicator and track when [Switch] is unchecked.
 * @param action Action to call to toggle when [Switch] is clicked, with argument as new `boolean` value
 */
@Composable
fun BasicSwitch(
	checked: Boolean,
	@SuppressLint("ModifierParameter") modifier: Modifier = Modifier.defaultLayoutPadding(Edge.HORIZONTAL),
	colorOn: Color = colors.selected,
	colorOff: Color = colors.navigation,
	action: (newBool: Boolean) -> Unit,
) {
	Switch(
		checked = checked,
		onCheckedChange = { action(it) },
		modifier = modifier,
		colors = SwitchDefaults.colors(
			checkedThumbColor = colorOff,
			checkedTrackColor = colorOn,
			checkedBorderColor = colorOn,
			uncheckedThumbColor = colorOn,
			uncheckedTrackColor = colorOff,
			uncheckedBorderColor = colorOff,
		),
	)
}
