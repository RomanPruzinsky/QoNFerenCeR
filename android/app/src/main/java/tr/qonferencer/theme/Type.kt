package tr.qonferencer.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import tr.qonferencer.R

/** Selectable app fonts */
enum class AvailableFonts(
	val family: FontFamily,
) {
	ROBOTO(FontFamily(Font(R.font.roboto_mono_bold, FontWeight.Light))),
	INTER(FontFamily(Font(R.font.inter, FontWeight.Medium))),
	SYNE(FontFamily(Font(R.font.syne, FontWeight.Medium))),
	PLAYFAIR(FontFamily(Font(R.font.playfair, FontWeight.Medium))),
	SPACE_GROTESK(FontFamily(Font(R.font.space_grotesk, FontWeight.Bold))),
	INFO_STORY(FontFamily(Font(R.font.info_story, FontWeight.Light))),
	PANGOLIN(FontFamily(Font(R.font.pangolin, FontWeight.Medium))),
	PATRICK_HAND(FontFamily(Font(R.font.patrick_hand, FontWeight.Medium))),
	SNIGLET(FontFamily(Font(R.font.sniglet, FontWeight.Medium))),
	STORY_SCRIPT(FontFamily(Font(R.font.story_script, FontWeight.Medium))),
	BITCOUNT(FontFamily(Font(R.font.bitcount_single, FontWeight.Medium))),
}

val DEFAULT_FONT = AvailableFonts.SNIGLET

/** Allowed range for typography's font size enlargement */
object FontSizeEnlarger {
	const val MAX = 7
	const val MIN = -5
}

/**
 * Builds [Typography] for current [color]/[family]/[fontSizeEnlarger]
 * ### Recommended usage:
 * ```
 * - display:   most important            > most important data, clock, welcome
 * - headline:  local state, section      > navigation, state
 * - title:     list item, dialog title   > list item, dialog title
 * - body:      text for reading          > text, longer data
 * - label:     buttons, image desc.      > buttons, interaction, system, images
 * ```
 */
fun createTypography(
	color: Color,
	family: FontFamily,
	fontSizeEnlarger: Int,
): Typography {
	fun buildStyle(
		size: Int,
		weight: FontWeight,
	) = TextStyle(
		fontSize = (size + fontSizeEnlarger).sp,
		color = color,
		fontFamily = family,
		fontWeight = weight,
		textAlign = TextAlign.Center,
		fontSynthesis = FontSynthesis.All,
	)
	
	return Typography(
		displayLarge = buildStyle(size = 36, weight = FontWeight.W900),
		displayMedium = buildStyle(size = 32, weight = FontWeight.W800),
		displaySmall = buildStyle(size = 28, weight = FontWeight.W800),
		
		headlineLarge = buildStyle(size = 26, weight = FontWeight.W900),
		headlineMedium = buildStyle(size = 22, weight = FontWeight.W800),
		headlineSmall = buildStyle(size = 20, weight = FontWeight.W800),
		
		titleLarge = buildStyle(size = 20, weight = FontWeight.W700),
		titleMedium = buildStyle(size = 18, weight = FontWeight.W700),
		titleSmall = buildStyle(size = 16, weight = FontWeight.W600),
		
		bodyLarge = buildStyle(size = 18, weight = FontWeight.W500),
		bodyMedium = buildStyle(size = 16, weight = FontWeight.W400),
		bodySmall = buildStyle(size = 14, weight = FontWeight.W400),
		
		labelLarge = buildStyle(size = 23, weight = FontWeight.W700),
		labelMedium = buildStyle(size = 18, weight = FontWeight.W600),
		labelSmall = buildStyle(size = 14, weight = FontWeight.W500),
	)
}

val typo
	@Composable
	get() = MaterialTheme.typography
