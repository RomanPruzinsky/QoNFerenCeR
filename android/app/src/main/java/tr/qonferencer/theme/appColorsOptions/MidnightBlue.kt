package tr.qonferencer.theme.appColorsOptions

import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.standardActionColors
import tr.qonferencer.theme.standardRoleColors
import tr.qonferencer.theme.standardScanResultColors

val midnightBlueAppColors = AppColors(
	appBackground = Color(0xFF000C1A),
	container = Color(0xFF001F3F),
	element = Color(0xFF003366),
	clickable = Color(0xFF0074D9),
	selected = Color(0xFF001A33),

	text = Color.White,
	navigation = Color.Black,

	level = standardRoleColors,
	scanResult = standardScanResultColors,
	action = standardActionColors,
)
