package tr.qonferencer.backend.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.shared.dtos.SlotDto
import tr.qonferencer.shared.enums.Role

/**
 * Info-desk lookup: finds the attendee the organizer is talking to
 *
 * Returns every plausible match and leaves the choice to the organizer, who can see each person's
 * data; the search does not have to be certain, it has to not miss anyone.
 *
 * Reading other people's names is gated twice: the role threshold and an explicit per-user grant.
 */
@Service
class AttendeeLookupService(
	private val users: UserRepository,
	private val anchors: UserAnchorService,
	private val caller: CallerService,
) {
	@Transactional(readOnly = true)
	fun search(query: String): List<SlotDto> {
		val allowed = caller.role().atLeast(Role.ORGANISER) && caller.canCheckByName()
		if (!allowed) throw forbidden("needs ORGANISER and canCheckByName")
		val trimmed = query.trim()
		if (trimmed.length < MIN_QUERY_LENGTH) return emptyList()
		return users.searchByName(trimmed, SIMILARITY_THRESHOLD, MAX_RESULTS)
			.map { SlotDto(it.id, it.fullName, customData = anchors.customData(it)) }
	}

	private companion object {
		/** A single letter would match most of the conference */
		const val MIN_QUERY_LENGTH = 2

		/** Measured: 0.5 still accepts a transposed pair of letters, the 0.6 default does not */
		const val SIMILARITY_THRESHOLD = 0.5

		const val MAX_RESULTS = 20
	}
}
