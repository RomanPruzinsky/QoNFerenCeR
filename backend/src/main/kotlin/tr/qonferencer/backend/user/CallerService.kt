package tr.qonferencer.backend.user

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import tr.qonferencer.backend.common.notFound
import tr.qonferencer.shared.enums.Role
import java.util.UUID

/** Processes caller of *this* request */
@Service
class CallerService(
	private val users: UserRepository,
) {
	fun userOrNull(): User? = jwtOrNull()?.let { users.findByKcSub(UUID.fromString(it.subject)) }
	fun requireUserId(): Long = (
		users.findByKcSub(UUID.fromString(jwt().subject))
			?: throw notFound("User doesn't exist")
		).id
	
	fun role(): Role = jwtOrNull()?.let { Role.highestAvailable(it.processKeycloakRoles()) } ?: Role.ANONYM
	fun isSpeaker(): Boolean = jwtOrNull()?.processIsSpeaker() ?: false
	fun canCheckByName(): Boolean = jwtOrNull()?.getClaim<Boolean>("canCheckByName") ?: false

// /////////////////// PUBLIC /////////////////////
// ////////////////////////////////////////////////
// ////////////////// HELPERS /////////////////////
	
	private fun jwt(): Jwt = jwtOrNull() ?: throw notFound("no authenticated principal")
	
	private fun jwtOrNull(): Jwt? = (SecurityContextHolder.getContext().authentication as? JwtAuthenticationToken)?.token
	
	@Suppress("UNCHECKED_CAST")
	private fun Jwt.processKeycloakRoles(): List<String> =
		(getClaimAsMap("realm_access")?.get("roles") as? List<String>).orEmpty()
	
	private fun Jwt.processIsSpeaker(): Boolean = getClaim<Boolean>("isSpeaker") ?: false
}
