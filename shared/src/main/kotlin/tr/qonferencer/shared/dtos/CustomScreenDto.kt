package tr.qonferencer.shared.dtos

/**
 * Custom screen introduction
 * @property id Screen id, used to fetch its content
 * @property titleKey Key to match for title translations
 */
data class CustomScreenDto(
	val id: String,
	val titleKey: String,
)
