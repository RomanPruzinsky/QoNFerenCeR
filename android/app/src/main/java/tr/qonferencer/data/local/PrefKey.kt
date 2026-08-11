package tr.qonferencer.data.local

/** Keys for [PrefsStorager] */
enum class PrefKey(
	val encrypted: Boolean,
) {
	ACCESS_TOKEN(true),
	REFRESH_TOKEN(true),
	MEAL_SECRET(true),
	APP_COLORS(false),
	APP_FONT_FAMILY(false),
	APP_FONT_SIZE(false),
	APP_LANGUAGE(false),
	TRANSLATIONS(false),
}
