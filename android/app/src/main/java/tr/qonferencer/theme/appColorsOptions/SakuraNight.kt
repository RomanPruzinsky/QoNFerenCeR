package tr.qonferencer.theme.appColorsOptions

import androidx.compose.ui.graphics.Color
import tr.qonferencer.theme.AppColors
import tr.qonferencer.theme.standardActionColors
import tr.qonferencer.theme.standardRoleColors
import tr.qonferencer.theme.standardScanResultColors

val sakuraNightAppColors = AppColors(
	appBackground = Color(0xFF1F0B10),
	container = Color(0xFF4A1E29),
	element = Color(0xFF7A2E41),
	clickable = Color(0xFFFFB7C5),
	selected = Color(0xFF2E0911),

	text = Color.White,
	navigation = Color.Black,

	level = standardRoleColors,
	scanResult = standardScanResultColors,
	action = standardActionColors,
)
