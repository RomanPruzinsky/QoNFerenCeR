package tr.qonferencer.theme.appColorsOptions

import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.standardActionColors
import tr.qonferencer.theme.standardRoleColors
import tr.qonferencer.theme.standardScanResultColors

val nsBlueAppColors = AppColors(
	appBackground = Color(0xFF4DB8FF),
	container = Color(0xFF2299FF),
	element = Color(0xFF0084FF),
	clickable = Color(0xFF0963EC),
	selected = Color(0xFF0003E0),

	text = Color.White,
	navigation = Color.Black,

	level = standardRoleColors,
	scanResult = standardScanResultColors,
	action = standardActionColors,
)
