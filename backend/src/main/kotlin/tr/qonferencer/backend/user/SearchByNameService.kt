package tr.qonferencer.backend.user

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.admin.KeycloakAdminService
import tr.qonferencer.backend.common.badRequest
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.shared.MIN_QUERY_LENGTH
import tr.qonferencer.shared.dtos.PageDto
import tr.qonferencer.shared.dtos.UserDisplayDto
import tr.qonferencer.shared.enums.Role

@Service
class SearchByNameService(
	private val users: UserRepository,
	private val kc: KeycloakAdminService,
	private val caller: CallerService,
) {
	@Transactional(readOnly = true)
	fun search(query: String, pageable: Pageable): PageDto<UserDisplayDto> {
		val role = caller.role()
		val allowed = (role == Role.ADMIN) || (role.atLeast(Role.ORGANISER) && caller.canCheckByName())
		if (!allowed) throw forbidden("needs ORGANISER with canCheckByName, or ADMIN")
		
		val trimmed = query.trim()
		if (role != Role.ADMIN && trimmed.length < MIN_QUERY_LENGTH) {
			throw badRequest("searchFor must be at least $MIN_QUERY_LENGTH characters")
		}
		
		val page = users.searchByName(trimmed, SIMILARITY_THRESHOLD, pageable).map {
			val info = kc.info(it.kcSub)
			UserDisplayDto(it.id, it.fullName, info.role, info.isSpeaker)
		}
		return PageDto(page.content, page.totalElements, page.totalPages, page.number, page.size)
	}
	
	private companion object {
		/** 0.5 still accepts transposed pair of letters, 0.6 default doesn't ("Nvoak" → "Novak" is valid now) */
		const val SIMILARITY_THRESHOLD = .5
	}
}
