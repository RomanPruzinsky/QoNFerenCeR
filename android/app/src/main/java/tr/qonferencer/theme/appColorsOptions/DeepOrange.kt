package tr.qonferencer.theme.appColorsOptions

import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.standardActionColors
import tr.qonferencer.theme.standardRoleColors
import tr.qonferencer.theme.standardScanResultColors

val deepOrangeAppColors = AppColors(
	appBackground = Color(0xFF1B0C00),
	container = Color(0xFF3D1C00),
	element = Color(0xFF663300),
	clickable = Color(0xFFFF9100),
	selected = Color(0xFFFF5500),

	text = Color.White,
	navigation = Color.Black,

	level = standardRoleColors,
	scanResult = standardScanResultColors,
	action = standardActionColors,
)
