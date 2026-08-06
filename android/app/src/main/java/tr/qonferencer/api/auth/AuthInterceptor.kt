package tr.qonferencer.api.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.HttpException
import tr.qonferencer.BuildConfig
import tr.qonferencer.data.remote.KeycloakApi

/** Adds bearer token to requests for backend */
class AuthInterceptor(
	private val tokenStore: AuthTokenHelper,
	private val keycloakApi: KeycloakApi,
) : Interceptor {
	companion object {
		private const val REFRESH_TOKEN_EXPIRED = 400
		private const val JWT_REJECTED = 401
	}
	
	override fun intercept(chain: Interceptor.Chain): Response {
		val savedToken = tokenStore.accessToken()
		val request = chain.request().withBearerToken(savedToken)
		
		val response = chain.proceed(request)
		// 401 without token means `not logged in` -> nothing to refresh
		if (response.code != JWT_REJECTED || savedToken == null) return response
		
		response.close()
		val refreshedToken = runBlocking { silentRefresh() } ?: return chain.proceed(request)
		return chain.proceed(request.withBearerToken(refreshedToken))
	}
	
	private fun Request.withBearerToken(token: String?): Request =
		if (token != null) newBuilder().header("Authorization", "Bearer $token").build() else this
	
	private suspend fun silentRefresh(): String? {
		val refresh = tokenStore.refreshToken() ?: return null
		return runCatching {
			val token = keycloakApi.token(
				BuildConfig.KEYCLOAK_REALM,
				mapOf(
					"grant_type" to "refresh_token",
					"client_id" to BuildConfig.KEYCLOAK_CLIENT_ID,
					"refresh_token" to refresh,
				),
			)
			tokenStore.updateTokens(token.accessToken, token.refreshToken ?: refresh)
			token.accessToken
		}.fold(
			onSuccess = { it },
			onFailure = { error ->
				if (error is HttpException && (error.code() == REFRESH_TOKEN_EXPIRED || error.code() == JWT_REJECTED)) {
					tokenStore.clearTokens()
					//TODO: now is logged out, tell it to user (toast for example)
				}
				null
			},
		)
	}
}
