package tr.qonferencer.data.local

/** Keys for [PrefsStorager] */
enum class PrefKey(
	val encrypted: Boolean,
) {
	ACCESS_TOKEN(true),
	REFRESH_TOKEN(true),
}
