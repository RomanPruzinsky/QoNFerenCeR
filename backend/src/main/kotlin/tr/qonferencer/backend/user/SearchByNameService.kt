package tr.qonferencer.backend.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.admin.KeycloakAdminService
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.shared.dtos.SearchByNameDisplayDto
import tr.qonferencer.shared.enums.Role

/** Info-desk lookup; returns every plausible match, gated by role and a per-user grant */
@Service
class SearchByNameService(
	private val users: UserRepository,
	private val kc: KeycloakAdminService,
	private val caller: CallerService,
) {
	@Transactional(readOnly = true)
	fun search(query: String, pageable: Pageable): Page<SearchByNameDisplayDto> {
		val allowed = caller.role().atLeast(Role.ORGANISER) && caller.canCheckByName()
		if (!allowed) throw forbidden("needs ORGANISER and canCheckByName")
		return users.searchByName(query.trim(), SIMILARITY_THRESHOLD, pageable).map {
			val info = kc.info(it.kcSub)
			SearchByNameDisplayDto(it.id, it.fullName, info.role, info.isSpeaker)
		}
	}

	private companion object {
		/** Measured: 0.5 still accepts a transposed pair of letters, the 0.6 default does not */
		const val SIMILARITY_THRESHOLD = 0.5
	}
}
