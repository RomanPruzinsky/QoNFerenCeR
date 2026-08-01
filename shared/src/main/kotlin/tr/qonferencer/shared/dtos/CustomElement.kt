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

	data class Text(
		val source: TextSource,
		val size: CustomTextSize = CustomTextSize.MEDIUM,
	) : CustomElement()

	data class Image(
		val url: String,
	) : CustomElement()

	data class Row(
		val children: List<CustomElement>,
	) : CustomElement()

	data class Column(
		val children: List<CustomElement>,
	) : CustomElement()
}
