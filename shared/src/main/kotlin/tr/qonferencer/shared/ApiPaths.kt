package tr.qonferencer.shared

/** REST paths shared by both clients */
object ApiPaths {
	private const val BASE = "/api/v$API_VERSION"

	object Splash {
		private const val ROOT = "$BASE/splash"
		const val ALL = "$ROOT/ALL"
	}

	object User {
		private const val ROOT = "$BASE/user"

		const val BY_ID = "$ROOT/{userId}"
		const val BY_NAME = "$ROOT/by-name"
	}

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

	object Meal {
		private const val ROOT = "$BASE/meal"

		const val MEAL_SCAN = "$ROOT/scan"
	}

	// TODO: translations management
}
