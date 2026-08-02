package tr.qonferencer.backend.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

/** Maps Keycloak realm roles from JWT into Spring `ROLE_*` authorities */
@Component
class JwtAuthConverter : Converter<Jwt, AbstractAuthenticationToken> {
	@Suppress("UNCHECKED_CAST")
	override fun convert(jwt: Jwt): AbstractAuthenticationToken {
		val realmAccess: Map<String, Any> = jwt.getClaimAsMap("realm_access") ?: emptyMap()
		val roles = (realmAccess["roles"] as? List<String>).orEmpty()
		val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }
		return JwtAuthenticationToken(jwt, authorities, jwt.subject)
	}
}
