package tr.qonferencer.theme.appColorsOptions

import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.standardActionColors
import tr.qonferencer.theme.standardRoleColors
import tr.qonferencer.theme.standardScanResultColors

val mochaCoffeeAppColors = AppColors(
	appBackground = Color(0xFF110A08),
	container = Color(0xFF2D1B14),
	element = Color(0xFF4E342E),
	clickable = Color(0xFFA1887F),
	selected = Color(0xFF1B0F0D),

	text = Color.White,
	navigation = Color.Black,

	level = standardRoleColors,
	scanResult = standardScanResultColors,
	action = standardActionColors,
)
