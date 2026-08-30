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
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserMealEntryDto
import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.ScannerType

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
			.andExpect(jsonPath("$.conferenceId").value("devconf-2026"))
			.andExpect(jsonPath("$.data.userId").value(42))
			.andExpect(jsonPath("$.data.meal.variantKey").value("meal.vegan"))
			.andRespond(withSuccess())
		
		listener(enabled = true).sendMessageToN8n(
			OutboundEvent.MealApproved(
				userId = 42L,
				meal = UserMealEntryDto(1L, "meal.vegan"),
				scannedBy = 99L,
				scannerType = ScannerType.QR,
			),
		)
		
		server.verify()
	}
	
	@Test
	fun `the shared secret rides along on every request`() {
		server.expect(requestTo("$BASE_URL/qonferencer_base/SLOT_CREATED"))
			.andExpect(header("QN-Token", "s3cret"))
			.andRespond(withSuccess())

		listener(enabled = true, token = "s3cret").sendMessageToN8n(
			OutboundEvent.SlotCreated(userId = 1L, username = "slot_001", userData = ModifyableUserDataDto("Roman")),
		)

		server.verify()
	}
	
	@Test
	fun `a webhook nobody created is not an error`() {
		server.expect(requestTo("$BASE_URL/qonferencer_base/APP_LAUNCHED"))
			.andRespond(withStatus(HttpStatus.NOT_FOUND))
		
		listener(enabled = true).sendMessageToN8n(OutboundEvent.AppLaunched(user = null))
		
		server.verify()
	}
	
	@Test
	fun `an n8n that answers with a server error is swallowed too`() {
		server.expect(requestTo("$BASE_URL/qonferencer_base/SLOT_CREATED"))
			.andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR))
		
		listener(enabled = true).sendMessageToN8n(
			OutboundEvent.SlotCreated(userId = 1L, username = "slot_001", userData = ModifyableUserDataDto("Roman")),
		)
		
		server.verify()
	}

	/** No expectation is registered, so any outgoing call would fail the mock server on its own */
	@Test
	fun `disabled outbound sends nothing at all`() {
		listener(enabled = false).sendMessageToN8n(
			OutboundEvent.MealDenied(
				userId = null,
				windowId = 1L,
				reason = MealScanResult.NO_USER_FOUND,
				scannedBy = 99L,
				scannerType = ScannerType.QR,
			),
		)
		
		server.verify()
	}
	
	private fun listener(
		enabled: Boolean,
		token: String = "test-secret",
	) = N8nOutboundListener(
		builder.build(),
		N8nProperties(
			enabled = enabled,
			baseUrl = BASE_URL,
			pathPrefix = "qonferencer_base",
			eventId = "devconf-2026",
			authToken = token,
			timeoutMs = 3000,
		),
	)
	
	private companion object {
		const val BASE_URL = "http://n8n.test/webhook"
	}
}
