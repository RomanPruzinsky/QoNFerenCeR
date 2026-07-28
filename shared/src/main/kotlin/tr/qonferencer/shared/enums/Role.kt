package tr.qonferencer.shared.enums

/** Linear role ladder; a higher role also holds every lower role's privileges */
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
		fun fromOrAnonym(value: String?): Role = entries.firstOrNull { it.name == value?.uppercase() } ?: ANONYM

		fun highest(roles: Collection<String>): Role = roles
			.map { fromOrAnonym(it) }
			.maxByOrNull { it.ordinal } ?: ANONYM
	}
}
