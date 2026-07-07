package tr.qonferencer.shared

/** REST paths shared by both clients */
object ApiPaths {
	private const val BASE = "/api/v1"

	object Me {
		const val ROOT = "$BASE/me"
		const val CONSENT = "$ROOT/consent"
	}

	const val SPLASH = "$BASE/splash"

	const val TRANSLATIONS = "$BASE/translations"

	const val CUSTOM_ELEMENT_DEFS = "$BASE/custom-element-defs"

	const val MEAL_SCAN = "$BASE/meal-scan"
}
