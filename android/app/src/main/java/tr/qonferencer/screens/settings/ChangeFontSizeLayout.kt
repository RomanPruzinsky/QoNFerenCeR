package tr.qonferencer.screens.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.theme.FontSizeEnlarger
import tr.qonferencer.theme.colors
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.theme.defaultLayoutPadding
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeFontSizeLayout() {
	val sliderColors = SliderDefaults.colors(
		thumbColor = colors.selected,
		activeTrackColor = colors.selected,
		inactiveTrackColor = colors.clickable,
		activeTickColor = Color.Transparent,
		inactiveTickColor = Color.Transparent,
	)

	SettingsCardLayout(dynamicTranslation("settings.fontSize")) {
		Slider(
			value = QoNFerenCeRApp.themePrefs.textSize.currentEnlargement.collectValue().toFloat(),
			onValueChange = { QoNFerenCeRApp.themePrefs.textSize.setEnlargement(it.roundToInt()) },
			valueRange = FontSizeEnlarger.MIN.toFloat()..FontSizeEnlarger.MAX.toFloat(),
			steps = (FontSizeEnlarger.MAX - FontSizeEnlarger.MIN) - 1,
			colors = sliderColors,
			track = { sliderState ->
				SliderDefaults.Track(
					sliderState = sliderState,
					colors = sliderColors,
					drawStopIndicator = null,
				)
			},
			modifier = Modifier
				.fillMaxWidth()
				.defaultLayoutPadding(),
		)
	}
}
