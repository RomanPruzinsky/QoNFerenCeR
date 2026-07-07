package tr.qonferencer.shared.enums

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

/** Result of meal scanning */
enum class MealScanResult {
	/** All good */
	APPROVED,

	/** Cannot get more portions */
	QUOTA_EXCEEDED,

	/** Scanned outside the meal's serving window (`startsAt`..`endsAt`) */
	OUT_OF_WINDOW,

	/** Didn't find user for this secret */
	NO_USER_FOUND,

	/** User doesn't have ordered portion for this meal */
	NOT_REGISTERED_PORTION,

	/** Anything else, fallback */
	@JsonEnumDefaultValue
	OTHER_ERROR,
}
