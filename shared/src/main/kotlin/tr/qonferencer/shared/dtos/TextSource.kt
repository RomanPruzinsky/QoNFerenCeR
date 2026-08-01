package tr.qonferencer.shared.dtos

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/** Source of a text element content */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes(
	JsonSubTypes.Type(value = TextSource.Ref::class, name = "REF"),
	JsonSubTypes.Type(value = TextSource.Link::class, name = "LINK"),
)
sealed class TextSource {
	/**
	 * Static text (translated)
	 * @property key Translation key
	 */
	data class Ref(
		val key: String,
	) : TextSource()

	/**
	 * Downloaded text (not translated)
	 * @property url Where to fetch text from
	 */
	data class Link(
		val url: String,
	) : TextSource()
}
