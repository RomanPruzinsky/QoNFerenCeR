package tr.qonferencer.backend.n8n

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.TestcontainersConfiguration
import tr.qonferencer.backend.meal.MealReservation
import tr.qonferencer.backend.meal.MealReservationRepository
import tr.qonferencer.backend.meal.MealSlotId
import tr.qonferencer.backend.meal.MealWindow
import tr.qonferencer.backend.meal.MealWindowRepository
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.MealScanRequestDto
import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.shared.enums.ScannerType
import tr.qonferencer.shared.scan.ScanToken
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

/** Which endpoints announce what; records published events, not the HTTP calls they become */
@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
@RecordApplicationEvents
@Transactional
class DomainEventPublishingTest {
	
	@Autowired
	private lateinit var mockMvc: MockMvc
	
	@Autowired
	private lateinit var objectMapper: ObjectMapper
	
	@Autowired
	private lateinit var events: ApplicationEvents
	
	@Autowired
	private lateinit var users: UserRepository
	
	@Autowired
	private lateinit var windows: MealWindowRepository
	
	@Autowired
	private lateinit var reservations: MealReservationRepository
	
	private val scannerSub = UUID.randomUUID()
	
	@BeforeEach
	fun createScanner() {
		users.insertIfAbsent(scannerSub, ByteArray(32), "Volunteer Scanner")
	}
	
	@Test
	fun `an anonymous launch carries no profile`() {
		mockMvc.get(ApiPaths.Splash.ALL).andExpect { status { isOk() } }

		val event = published().single() as OutboundEvent.AppLaunched
		assertEquals(null, event.user)
	}

	@Test
	fun `a logged-in launch carries the caller's own profile`() {
		val sub = UUID.randomUUID()
		users.insertIfAbsent(sub, ByteArray(32), "Hungry Attendee")
		val userId = users.findByKcSub(sub)!!.id

		mockMvc.get(ApiPaths.Splash.ALL) {
			with(
				jwt().jwt {
					it.subject(sub.toString())
						.claim("realm_access", mapOf("roles" to listOf(Role.VISITOR.name)))
				},
			)
		}.andExpect { status { isOk() } }

		val event = published().single() as OutboundEvent.AppLaunched
		assertEquals(userId, event.user?.userId)
		assertEquals("Hungry Attendee", event.user?.fullName)
	}

	@Test
	fun `a handed out meal announces who got what`() {
		val secret = ByteArray(32) { 7 }
		val userId = newUser(secret)
		val windowId = newWindowWithPortion(userId, "meal.vegan")
		val token = ScanToken.build(userId, secret, Instant.now().epochSecond)
		
		scan(MealScanRequestDto(token, windowId, UUID.randomUUID(), ScannerType.QR)).andExpect { status { isOk() } }
		
		val event = published().filterIsInstance<OutboundEvent.MealApproved>().single()
		assertEquals(userId, event.userId)
		assertEquals(windowId, event.meal.windowId)
		assertEquals("meal.vegan", event.meal.variantKey)
		assertEquals(ScannerType.QR, event.scannerType)
	}

	/** The organizer has to be able to tell a cryptographic scan from a copyable badge */
	@Test
	fun `a badge scan says so, so the weaker ones can be counted`() {
		val userId = newUser(ByteArray(32) { 6 })
		val windowId = newWindowWithPortion(userId, "meal.regular")
		
		scan(MealScanRequestDto(userId.toString(), windowId, UUID.randomUUID(), ScannerType.BARCODE))
			.andExpect { status { isOk() } }
		
		val event = published().filterIsInstance<OutboundEvent.MealApproved>().single()
		assertEquals(ScannerType.BARCODE, event.scannerType)
	}
	
	@Test
	fun `a refused scan announces the reason, since nothing is written down`() {
		val secret = ByteArray(32) { 5 }
		val userId = newUser(secret)
		val windowId = windows.save(newWindow()).id
		val token = ScanToken.build(userId, secret, Instant.now().epochSecond)
		
		scan(MealScanRequestDto(token, windowId, UUID.randomUUID(), ScannerType.QR)).andExpect { status { isOk() } }
		
		val event = published().filterIsInstance<OutboundEvent.MealDenied>().single()
		assertEquals(MealScanResult.NOT_REGISTERED_PORTION, event.reason)
		assertEquals(userId, event.userId)
	}
	
	private fun published(): List<OutboundEvent> = events.stream(OutboundEvent::class.java).toList()
	
	private fun scan(request: MealScanRequestDto) = mockMvc.post(ApiPaths.Meal.MEAL_SCAN) {
		with(
			jwt().jwt {
				it.subject(scannerSub.toString())
					.claim("realm_access", mapOf("roles" to listOf(Role.VOLUNTEER.name)))
			},
		)
		contentType = MediaType.APPLICATION_JSON
		content = objectMapper.writeValueAsString(request)
	}
	
	private fun newUser(secret: ByteArray): Long {
		val sub = UUID.randomUUID()
		users.insertIfAbsent(sub, secret, "Hungry Attendee")
		return users.findByKcSub(sub)!!.id
	}
	
	private fun newWindowWithPortion(
		userId: Long,
		variantKey: String,
	): Long {
		val windowId = windows.save(newWindow()).id
		reservations.save(MealReservation(MealSlotId(userId, windowId), variantKey))
		return windowId
	}
	
	private fun newWindow() = MealWindow(0, "meal.test", Instant.now(), Instant.now().plusSeconds(3600))
}
