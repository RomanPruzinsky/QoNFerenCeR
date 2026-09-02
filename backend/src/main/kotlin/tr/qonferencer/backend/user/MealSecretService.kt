package tr.qonferencer.backend.user

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import tr.qonferencer.backend.common.forbidden
import java.util.Base64

@Service
class MealSecretService(
	private val caller: CallerService,
	restClientBuilder: RestClient.Builder,
	@param:Value($$"${qonferencer.keycloak.server-url}") private val serverUrl: String,
	@param:Value($$"${qonferencer.keycloak.realm}") private val realm: String,
	@param:Value($$"${qonferencer.keycloak.public-client-id}") private val publicClientId: String,
) {
	private val restClient = restClientBuilder.build()

	fun reveal(password: String): String {
		val user = caller.requireUser()
		verifyPassword(caller.username(), password)
		return Base64.getEncoder().encodeToString(user.mealSecret)
	}

	/** @throws tr.qonferencer.backend.common.ApiException 403 if Keycloak rejects [username]/[password], rethrows other errors */
	private fun verifyPassword(
		username: String,
		password: String,
	) {
		val form = LinkedMultiValueMap<String, String>().apply {
			add("grant_type", "password")
			add("client_id", publicClientId)
			add("username", username)
			add("password", password)
		}

		try {
			restClient.post()
				.uri("$serverUrl/realms/$realm/protocol/openid-connect/token")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(form)
				.retrieve()
				.toBodilessEntity()
		} catch (ex: HttpClientErrorException) {
			if (ex.statusCode == HttpStatus.BAD_REQUEST && ex.responseBodyAsString.contains("\"error\":\"invalid_grant\"")) {
				throw forbidden("wrong password")
			}
			throw ex
		}
	}
}
