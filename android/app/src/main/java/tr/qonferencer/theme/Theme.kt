package tr.qonferencer.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.theme.color.LocalAppColors
import tr.qonferencer.translations.LocalLanguage
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.theme.setNavigationBarColor
import tr.qonferencer.trons.theme.setStatusBarColor

@Composable
fun QoNFerenCeRTheme(content: @Composable () -> Unit) {
	val currentColors = QoNFerenCeRApp.themePrefs.colors.currentColors.collectValue().colors
	val currentFont by QoNFerenCeRApp.themePrefs.font.currentFont.collectAsState()
	val currentEnlargement by QoNFerenCeRApp.themePrefs.textSize.currentEnlargement.collectAsState()
	
	val colorScheme = darkColorScheme(
		primary = currentColors.selected,
		secondary = currentColors.clickable,
		background = currentColors.appBackground,
		surface = currentColors.container,
		onPrimary = currentColors.text,
		onSecondary = currentColors.text,
		onBackground = currentColors.text,
		onSurface = currentColors.text,
	)
	
	val typography = remember(currentColors.text, currentFont, currentEnlargement) {
		createTypography(currentColors.text, currentFont.family, currentEnlargement)
	}
	
	val view = LocalView.current
	if (!view.isInEditMode) {
		SideEffect {
			val window = (view.context as Activity).window
			window.setStatusBarColor(currentColors.appBackground)
			window.setNavigationBarColor(currentColors.navigation)
			WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
				currentColors.appBackground.luminance() > 0.5F
		}
	}
	
	CompositionLocalProvider(
		LocalAppColors provides currentColors,
		LocalLanguage provides QoNFerenCeRApp.language.current.collectValue(),
	) {
		MaterialTheme(
			colorScheme = colorScheme,
			typography = typography,
			content = content,
		)
	}
}
