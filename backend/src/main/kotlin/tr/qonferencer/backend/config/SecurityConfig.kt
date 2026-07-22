package tr.qonferencer.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtIssuerValidator
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import tr.qonferencer.backend.user.CallerService
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.enums.Role

/**
 * Spring security configuration
 *
 * Sets up as OAuth2 resource server that trusts JWT tokens
 *
 * `@Configuration`: Spring bean-definition class
 *
 * Authorization beyond "is authenticated" belongs in the services, as a `Role.atLeast` threshold;
 * `hasRole` would compare realm roles flat and miss the linear ladder.
 * @param jwkSetUri Where to fetch Keycloak's public signing keys
 * @param issuer Public issuer string tokens must claim
 */
@Configuration
class SecurityConfig(
	@param:Value($$"${qonferencer.keycloak.jwk-set-uri}") private val jwkSetUri: String,
	@param:Value($$"${qonferencer.keycloak.issuer}") private val issuer: String,
) {

	@Bean
	fun securityFilterChain(http: HttpSecurity, converter: JwtAuthConverter, caller: CallerService): SecurityFilterChain {
		http
			.csrf { it.disable() }
			.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // Server remembers nothing
			.authorizeHttpRequests { reg ->
				reg.requestMatchers(
					"/api/v1/splash",
					"/api/v1/custom-screens/**",
				).permitAll() // Public endpoints
					.requestMatchers("/actuator/health/**").permitAll()
					.requestMatchers("${ApiPaths.Admin.ROOT}/**").access(minRole(caller, Role.ADMIN))
					.anyRequest().authenticated()
			}
			.oauth2ResourceServer { rs ->
				rs.jwt { it.decoder(jwtDecoder()).jwtAuthenticationConverter(converter) }
			}
		return http.build()
	}

	/** Threshold gate for a whole path prefix, so a newly added endpoint under it is covered by default */
	private fun minRole(caller: CallerService, min: Role) = AuthorizationManager<RequestAuthorizationContext> { _, _ ->
		AuthorizationDecision(caller.activeRole().atLeast(min))
	}

	/** Custom JWT validation */
	@Bean
	fun jwtDecoder(): JwtDecoder {
		val decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
		decoder.setJwtValidator(
			DelegatingOAuth2TokenValidator(
				JwtValidators.createDefault(),
				JwtIssuerValidator(issuer),
			),
		)
		return decoder
	}
}
