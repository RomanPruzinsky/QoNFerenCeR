package tr.qonferencer.theme.appColorsOptions

import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.standardActionColors
import tr.qonferencer.theme.standardRoleColors
import tr.qonferencer.theme.standardScanResultColors

val deepSeaAppColors = AppColors(
	appBackground = Color(0xFF000510),
	container = Color(0xFF00122A),
	element = Color(0xFF002244),
	clickable = Color(0xFF00D4FF),
	selected = Color(0xFF003D7C),

	text = Color.White,
	navigation = Color.Black,

	level = standardRoleColors,
	scanResult = standardScanResultColors,
	action = standardActionColors,
)
