package tr.qonferencer.shared.dtos

/** Custom pagination */
data class PageDto<T>(
	val content: List<T>,
	val totalElements: Long,
	val totalPages: Int,
	val number: Int,
	val size: Int,
)
