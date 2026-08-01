package tr.qonferencer.shared.enums

import java.util.Locale

/**
 * Available user's roles
 *
 * Higher role also holds every lower role's privileges
 */
enum class Role {
	ANONYM,
	VISITOR,
	VOLUNTEER,
	LEADER,
	ORGANISER,
	ADMIN,
	;

	/** Whether can access [min]'s privileges */
	fun atLeast(min: Role): Boolean = ordinal >= min.ordinal
	
	companion object {
		/** @return Parsed [Role] or ANONYM as fallback */
		fun fromOrAnonym(value: String?): Role = entries.firstOrNull { it.name == value?.uppercase(Locale.ROOT) } ?: ANONYM

		/** @return Highest [Role] from [roles] */
		fun highestAvailable(roles: Collection<String>): Role = roles
			.map { fromOrAnonym(it) }
			.maxByOrNull { it.ordinal } ?: ANONYM
	}
}
