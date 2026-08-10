package tr.qonferencer.theme.appColorsOptions

import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.standardActionColors
import tr.qonferencer.theme.standardRoleColors
import tr.qonferencer.theme.standardScanResultColors

val blackPinkAppColors = AppColors(
	appBackground = Color(0xFF000000),
	container = Color(0xFF2D0219),
	element = Color(0xFF4D022A),
	clickable = Color(0xFFFF007F),
	selected = Color(0xFFAD1457),

	text = Color.White,
	navigation = Color.Black,

	level = standardRoleColors,
	scanResult = standardScanResultColors,
	action = standardActionColors,
)
