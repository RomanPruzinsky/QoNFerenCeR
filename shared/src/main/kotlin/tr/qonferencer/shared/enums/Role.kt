package tr.qonferencer.shared.enums

/**
 * Linear roles order
 *
 * Higher role has also lower privileges (`LEADER` has `GUEST`+`VISITOR`+`VOLUNTEER`+`LEADER` privileges)
 */
enum class Role {
	GUEST,
	VISITOR,
	VOLUNTEER,
	LEADER,
	ORGANISER,
	ADMIN,
	;

	/** Whether can access [min]'s privileges */
	fun atLeast(min: Role): Boolean = ordinal >= min.ordinal

	companion object {
		fun fromOrGuest(value: String?): Role = entries.firstOrNull { it.name == value?.uppercase() } ?: GUEST

		fun highest(roles: Collection<String>): Role = roles
			.map { fromOrGuest(it) }
			.maxByOrNull { it.ordinal } ?: GUEST
	}
}
