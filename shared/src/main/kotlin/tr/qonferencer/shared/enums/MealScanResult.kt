package tr.qonferencer.shared.enums

/** Result of meal scanning */
enum class MealScanResult {
	/** All good */
	APPROVED,

	/** Already ate in this window */
	ALREADY_CONSUMED,

	/** Didn't find user for this secret */
	NO_USER_FOUND,

	/** User doesn't have ordered portion for this meal */
	NOT_REGISTERED_PORTION,
}
