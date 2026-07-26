package tr.qonferencer.backend.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.shared.dtos.SlotDto
import tr.qonferencer.shared.enums.Role

/** Info-desk lookup; returns every plausible match, gated by role and a per-user grant */
@Service
class SearchByNameService(
	private val users: UserRepository,
	private val anchors: UserAnchorService,
	private val caller: CallerService,
) {
	@Transactional(readOnly = true)
	fun search(query: String): List<SlotDto> {
		val allowed = caller.role().atLeast(Role.ORGANISER) && caller.canCheckByName()
		if (!allowed) throw forbidden("needs ORGANISER and canCheckByName")
		return users.searchByName(query.trim(), SIMILARITY_THRESHOLD)
			.map { SlotDto(it.id, it.fullName, customData = anchors.customData(it)) }
	}

	private companion object {
		/** Measured: 0.5 still accepts a transposed pair of letters, the 0.6 default does not */
		const val SIMILARITY_THRESHOLD = 0.5
	}
}
