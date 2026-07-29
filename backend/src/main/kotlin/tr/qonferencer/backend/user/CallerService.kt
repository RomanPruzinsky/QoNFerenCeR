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
	fun kcSub(): UUID = UUID.fromString(jwt().subject)

	fun role(): Role = Role.highest(jwt().processKeycloakRoles())

	fun isSpeaker(): Boolean = jwt().processIsSpeaker()

	/** Explicit per-user grant for the info-desk lookup, required on top of the role threshold */
	fun canCheckByName(): Boolean = jwtOrNull()?.getClaim<Boolean>("canCheckByName") ?: false

	fun activeRole(): Role = jwtOrNull()?.let { Role.highest(it.processKeycloakRoles()) } ?: Role.ANONYM

	fun activeIsSpeaker(): Boolean = jwtOrNull()?.processIsSpeaker() ?: false

	fun requireAppUser(): User = users.findByKcSub(kcSub()) ?: throw notFound("app_user does not exist")

	fun appUserId(): Long = requireAppUser().id

	/** The caller's own anchor, or null when unauthenticated or not yet provisioned */
	fun activeAppUser(): User? = jwtOrNull()?.let { users.findByKcSub(UUID.fromString(it.subject)) }

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
