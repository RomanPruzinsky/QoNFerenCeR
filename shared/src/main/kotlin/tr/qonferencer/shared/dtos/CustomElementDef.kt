package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.FieldType

/**
 * Custom element, drawn by renderer
 * @property key Field key into customJson
 * @property labelKey Key for translation
 * @property type Type to draw
 * @property optionsJson If [type] is [FieldType.SELECT], options to draw
 * @property required Whether it is required to fill this data
 * @property orderIndex Order of element on screen
 */
data class CustomElementDef(
	val key: String,
	val labelKey: String,
	val type: FieldType,
	val optionsJson: List<String>? = null,
	val required: Boolean = false,
	val orderIndex: Int = Int.MAX_VALUE,
)
