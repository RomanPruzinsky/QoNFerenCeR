package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.Role

/**
 * Custom screen, full admin view
 * @property id Screen id, used to fetch its content
 * @property titleKey Key to match for title translations
 * @property icon Key into client's icon options
 * @property minRole Minimum role required to see screen
 * @property body Displayed list of [CustomElement]
 */
data class CustomScreenAdminDto(
	val id: String,
	val titleKey: String,
	val icon: String,
	val minRole: Role,
	val body: List<CustomElement>,
)
