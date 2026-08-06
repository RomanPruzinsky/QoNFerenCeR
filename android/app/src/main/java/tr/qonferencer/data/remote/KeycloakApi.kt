package tr.qonferencer.data.remote

import com.fasterxml.jackson.annotation.JsonProperty
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

/** Direct calls to Keycloak's endpoint */
interface KeycloakApi {
	@FormUrlEncoded
	@POST("realms/{realm}/protocol/openid-connect/token")
	suspend fun token(@Path("realm") realm: String, @FieldMap fields: Map<String, String>): TokenDto
	
	@FormUrlEncoded
	@POST("realms/{realm}/protocol/openid-connect/logout")
	suspend fun logout(
		@Path("realm") realm: String,
		@Field("client_id") clientId: String,
		@Field("refresh_token") refreshToken: String,
	): Response<Unit>
}

data class TokenDto(
	@JsonProperty("access_token") val accessToken: String,
	@JsonProperty("refresh_token") val refreshToken: String?,
	@JsonProperty("expires_in") val expiresIn: Int?,
)
