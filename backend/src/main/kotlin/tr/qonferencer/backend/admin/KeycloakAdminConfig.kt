package tr.qonferencer.backend.admin

import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/** Builds the Keycloak Admin API client (service account, client credentials) */
@Configuration
class KeycloakAdminConfig(
	@param:Value($$"${qonferencer.keycloak.admin.server-url}") private val serverUrl: String,
	@param:Value($$"${qonferencer.keycloak.admin.realm}") private val realm: String,
	@param:Value($$"${qonferencer.keycloak.admin.client-id}") private val clientId: String,
	@param:Value($$"${qonferencer.keycloak.admin.client-secret}") private val clientSecret: String,
) {
	@Bean
	fun keycloakAdmin(): Keycloak = KeycloakBuilder.builder()
		.serverUrl(serverUrl)
		.realm(realm)
		.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
		.clientId(clientId)
		.clientSecret(clientSecret)
		.build()
}
