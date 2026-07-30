package tr.qonferencer.shared.dtos

/**
 * Custom screen descriptor, body fetched by [id]
 * @property id Screen id, used to fetch its content
 * @property titleKey Key to match for translations
 */
data class CustomScreenDto(
	val id: String,
	val titleKey: String,
)
