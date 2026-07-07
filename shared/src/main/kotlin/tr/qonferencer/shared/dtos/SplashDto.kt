package tr.qonferencer.shared.dtos

/**
 * Data loaded at app's start
 * @property version Content hash
 * @property customScreens List of custom screens to draw
 * @property customElementDefs List of custom elements definitions
 */
data class SplashDto(
	val version: String? = null,
	val customScreens: List<CustomScreenDto> = emptyList(),
	val customElementDefs: List<CustomElementDef> = emptyList(),
)
