package tr.qonferencer.theme

import tr.qonferencer.data.local.PrefsStorager

/** Bundles user's appearance settings: [colors], [font], [textSize] */
class ThemePrefs(
	prefsStorager: PrefsStorager,
) {
	val colors = ColorPrefs(prefsStorager)
	val font = FontPrefs(prefsStorager)
	val textSize = TextSizePrefs(prefsStorager)
}
