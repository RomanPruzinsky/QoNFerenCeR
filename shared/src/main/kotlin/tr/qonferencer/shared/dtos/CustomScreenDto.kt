package tr.qonferencer.shared.dtos

import tr.qonferencer.shared.enums.ScreenTemplate

/**
 * Custom screen data
 * @property id Screen id
 * @property titleKey Key to match for translations
 * @property template How should screen render
 * @property orderIndex Order in app-menu
 */
data class CustomScreenDto(
	val id: String,
	val titleKey: String,
	val template: ScreenTemplate,
	val orderIndex: Int = Int.MAX_VALUE,
)

// TODO: use for android
fun List<CustomScreenDto>.sort() = this.sortedWith(compareBy({ it.orderIndex }, { it.id }))
