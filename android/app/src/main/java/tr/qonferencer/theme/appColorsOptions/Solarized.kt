package tr.qonferencer.theme.appColorsOptions

import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.standardActionColors
import tr.qonferencer.theme.standardRoleColors
import tr.qonferencer.theme.standardScanResultColors

val solarizedAppColors = AppColors(
	appBackground = Color(0xFF002B36),
	container = Color(0xFF073642),
	element = Color(0xFF586E75),
	clickable = Color(0xFF268BD2),
	selected = Color(0xFF001E26),

	text = Color.White,
	navigation = Color.Black,

	level = standardRoleColors,
	scanResult = standardScanResultColors,
	action = standardActionColors,
)
