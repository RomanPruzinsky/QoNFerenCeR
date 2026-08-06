package tr.qonferencer.api.auth

import tr.qonferencer.BuildConfig
import tr.qonferencer.data.remote.KeycloakApi

/** Keycloak's login/logout */
class AuthRepository(
	private val keycloakApi: KeycloakApi,
	private val tokenStore: AuthTokenHelper,
) {
	fun isLoggedIn(): Boolean = tokenStore.isLoggedIn()
	
	suspend fun login(username: String, password: String) {
		val token = keycloakApi.token(
			BuildConfig.KEYCLOAK_REALM,
			mapOf(
				"grant_type" to "password",
				"client_id" to BuildConfig.KEYCLOAK_CLIENT_ID,
				"username" to username,
				"password" to password,
			),
		)
		tokenStore.updateTokens(token.accessToken, token.refreshToken)
	}
	
	suspend fun logout() {
		tokenStore.refreshToken()?.let { refreshToken ->
			runCatching {
				keycloakApi.logout(
					realm = BuildConfig.KEYCLOAK_REALM,
					clientId = BuildConfig.KEYCLOAK_CLIENT_ID,
					refreshToken = refreshToken,
				)
			}
		}
		tokenStore.clearTokens()
	}
}
