package tr.qonferencer.screens.mealScan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tr.qonferencer.screens.keyInputMethod.KeyInputMethod
import tr.qonferencer.theme.colors
import tr.qonferencer.trons.theme.defaultBorder
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.orTransparentIf

@Composable
fun MealScanMethodPicker(
	selectedMethod: KeyInputMethod,
	onMethodSelect: (KeyInputMethod) -> Unit,
) {
	val availableMethods = KeyInputMethod.entries
	
	Row(
		horizontalArrangement = Arrangement.SpaceEvenly,
		modifier = Modifier
			.fillMaxWidth()
			.height(IntrinsicSize.Max)
			.defaultLayoutPadding()
			.defaultBorder(),
	) {
		availableMethods.forEach { method ->
			Icon(
				imageVector = method.icon,
				contentDescription = method.labelKey,
				tint = colors.text,
				modifier = Modifier
					.size(defaultIconSizeLarge)
					.weight(1F)
					.fillMaxHeight()
					.background(colors.clickable.orTransparentIf(selectedMethod != method))
					.clickable { onMethodSelect(method) }
					.defaultLayoutPadding(),
			)
			
			if (method != availableMethods.last()) {
				VerticalDivider(
					modifier = Modifier.fillMaxHeight(),
					color = colors.text,
				)
			}
		}
	}
}
