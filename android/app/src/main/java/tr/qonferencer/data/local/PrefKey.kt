package tr.qonferencer.data.local

/** Keys for [PrefsStorager] */
enum class PrefKey(
	val encrypted: Boolean,
) {
	ACCESS_TOKEN(true),
	REFRESH_TOKEN(true),
	APP_THEME(false),
	APP_FONT_FAMILY(false),
	APP_FONT_SIZE(false),
}
