package tr.qonferencer.backend.n8n

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.content
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient

/** Delivery mechanics, invoked straight rather than through `@Async` so assertions cannot race */
class N8nDeliveryTest {

	private val builder = RestClient.builder().baseUrl(BASE_URL).messageConverters { converters ->
		converters.removeIf { it is MappingJackson2HttpMessageConverter }
		converters.add(MappingJackson2HttpMessageConverter(ObjectMapper().registerModule(JavaTimeModule())))
	}
	private val server = MockRestServiceServer.bindTo(builder).build()

	@Test
	fun `event goes to the path named after it, wrapped in the envelope`() {
		server.expect(requestTo("$BASE_URL/qonferencer_base/MEAL_APPROVED"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.schemaVersion").value(1))
			.andExpect(jsonPath("$.eventType").value("MEAL_APPROVED"))
			.andExpect(jsonPath("$.event").value("devconf-2026"))
			.andExpect(jsonPath("$.data.userId").value(42))
			.andExpect(jsonPath("$.data.variantKey").value("meal.vegan"))
			.andRespond(withSuccess())

		listener(enabled = true).onEvent(
			N8nEvent(EventType.MEAL_APPROVED, mapOf("userId" to 42L, "variantKey" to "meal.vegan")),
		)

		server.verify()
	}

	@Test
	fun `the shared secret rides along when one is configured`() {
		server.expect(requestTo("$BASE_URL/qonferencer_base/SLOT_CREATED"))
			.andExpect(header("QN-Token", "s3cret"))
			.andRespond(withSuccess())

		listener(enabled = true, token = "s3cret").onEvent(N8nEvent(EventType.SLOT_CREATED, mapOf("userId" to 1L)))

		server.verify()
	}

	@Test
	fun `no secret configured means no header, not an empty one`() {
		server.expect(requestTo("$BASE_URL/qonferencer_base/SLOT_CREATED"))
			.andExpect(headerDoesNotExist("QN-Token"))
			.andRespond(withSuccess())

		listener(enabled = true).onEvent(N8nEvent(EventType.SLOT_CREATED, mapOf("userId" to 1L)))

		server.verify()
	}

	@Test
	fun `a webhook nobody created is not an error`() {
		server.expect(requestTo("$BASE_URL/qonferencer_base/APP_LAUNCHED"))
			.andRespond(withStatus(HttpStatus.NOT_FOUND))

		listener(enabled = true).onEvent(N8nEvent(EventType.APP_LAUNCHED, mapOf("role" to "ANONYM")))

		server.verify()
	}

	@Test
	fun `an n8n that answers with a server error is swallowed too`() {
		server.expect(requestTo("$BASE_URL/qonferencer_base/SLOT_CREATED"))
			.andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR))

		listener(enabled = true).onEvent(N8nEvent(EventType.SLOT_CREATED, mapOf("userId" to 1L)))

		server.verify()
	}

	/** No expectation is registered, so any outgoing call would fail the mock server on its own */
	@Test
	fun `disabled outbound sends nothing at all`() {
		listener(enabled = false).onEvent(N8nEvent(EventType.MEAL_DENIED, mapOf("reason" to "NO_USER_FOUND")))

		server.verify()
	}

	private fun listener(enabled: Boolean, token: String = "") = N8nOutboundListener(
		builder.build(),
		N8nProperties(enabled = enabled, baseUrl = BASE_URL, eventName = "devconf-2026", pathToken = token),
	)

	private companion object {
		const val BASE_URL = "http://n8n.test/webhook"
	}
}
