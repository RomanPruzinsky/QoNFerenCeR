package tr.qonferencer.theme.appColorsOptions

import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.standardActionColors
import tr.qonferencer.theme.standardRoleColors
import tr.qonferencer.theme.standardScanResultColors

val forestGreenAppColors = AppColors(
	appBackground = Color(0xFF000000),
	container = Color(0xFF012A01),
	element = Color(0xFF024F02),
	clickable = Color(0xFF04B733),
	selected = Color(0xFF008F11),

	text = Color.White,
	navigation = Color.Black,

	level = standardRoleColors,
	scanResult = standardScanResultColors,
	action = standardActionColors,
)
