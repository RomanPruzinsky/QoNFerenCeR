package tr.qonferencer.shared

/** REST paths shared by both clients */
object ApiPaths {
	private const val BASE = "/api/v$API_VERSION"

	const val USER_BY_ID = "$BASE/users/{userId}"

	object CustomScreens {
		const val ROOT = "$BASE/custom-screens"
		const val BY_ID = "$ROOT/{id}"
	}

	object Admin {
		const val ROOT = "$BASE/admin"

		const val ADD_USER = "$ROOT/add-user"
		const val UPDATE_USER = "$ROOT/update-user/{userId}"
		const val DELETE_USER = "$ROOT/delete/{userId}"

		const val LOGIN = "$ROOT/login/{userId}"
		const val REVOKE = "$ROOT/revoke/{userId}"
	}

	const val SEARCH_BY_NAME = "$BASE/search-by-name"

	const val SPLASH = "$BASE/splash"

	const val MEAL_SCAN = "$BASE/meal-scan"
}
