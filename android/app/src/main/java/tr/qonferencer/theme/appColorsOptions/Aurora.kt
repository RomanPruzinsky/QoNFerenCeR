package tr.qonferencer.theme.appColorsOptions

import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.standardActionColors
import tr.qonferencer.theme.standardRoleColors
import tr.qonferencer.theme.standardScanResultColors

val auroraAppColors = AppColors(
	appBackground = Color(0xFF020024),
	container = Color(0xFF090979),
	element = Color(0xFF003566),
	clickable = Color(0xFF04B5D9),
	selected = Color(0xFF7000FF),

	text = Color.White,
	navigation = Color.Black,

	level = standardRoleColors,
	scanResult = standardScanResultColors,
	action = standardActionColors,
)
