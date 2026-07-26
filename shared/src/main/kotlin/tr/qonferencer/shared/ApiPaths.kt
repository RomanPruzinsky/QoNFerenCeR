package tr.qonferencer.shared

/** REST paths shared by both clients */
object ApiPaths {
	private const val BASE = "/api/v1"

	object Me {
		const val ROOT = "$BASE/me"
	}

	object CustomScreens {
		const val ROOT = "$BASE/custom-screens"
		const val BY_ID = "$ROOT/{id}"
	}

	object Admin {
		const val ROOT = "$BASE/admin"
		const val SLOTS = "$ROOT/slots"
		const val SLOT_BY_ID = "$SLOTS/{userId}"
		const val SLOT_LOGIN = "$SLOTS/{userId}/login"
		const val SLOT_REVOKE = "$SLOTS/{userId}/revoke"
	}

	/** Info-desk search for the attendee the organizer is talking to */
	const val SEARCH_BY_NAME = "$BASE/search-by-name"

	const val SPLASH = "$BASE/splash"

	const val MEAL_SCAN = "$BASE/meal-scan"
}
