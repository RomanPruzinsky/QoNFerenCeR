package tr.qonferencer.shared.dtos

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import tr.qonferencer.shared.enums.CustomTextSize

// TODO: dynamic list — no primitive repeats a template per data row, so fed screens stay static

/** Custom element, drawn by renderer in received order */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
	JsonSubTypes.Type(value = CustomElement.Text::class, name = "TEXT"),
	JsonSubTypes.Type(value = CustomElement.Image::class, name = "IMAGE"),
	JsonSubTypes.Type(value = CustomElement.Row::class, name = "ROW"),
	JsonSubTypes.Type(value = CustomElement.Column::class, name = "COLUMN"),
)
sealed class CustomElement {

	/**
	 * Text element
	 * @property source Where text comes from
	 * @property size Text size
	 */
	data class Text(
		val source: TextSource,
		val size: CustomTextSize = CustomTextSize.MEDIUM,
	) : CustomElement()

	/**
	 * Image element
	 * @property url Image location
	 */
	data class Image(
		val url: String,
	) : CustomElement()

	/**
	 * Horizontal container
	 * @property children Nested elements
	 */
	data class Row(
		val children: List<CustomElement>,
	) : CustomElement()

	/**
	 * Vertical container
	 * @property children Nested elements
	 */
	data class Column(
		val children: List<CustomElement>,
	) : CustomElement()
}
