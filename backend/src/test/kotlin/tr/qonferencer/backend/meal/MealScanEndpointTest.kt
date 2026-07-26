package tr.qonferencer.backend.meal

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.TestcontainersConfiguration
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.MealScanRequestDto
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.shared.enums.ScanCarrier
import tr.qonferencer.shared.scan.ScanToken
import java.time.Instant
import java.util.UUID

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MealScanEndpointTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Autowired
	private lateinit var windows: MealWindowRepository

	@Autowired
	private lateinit var reservations: MealReservationRepository

	@Autowired
	private lateinit var users: UserRepository

	private val scannerSub = UUID.randomUUID()

	@BeforeEach
	fun createScanner() {
		users.insertIfAbsent(scannerSub, ByteArray(32), "Volunteer Scanner")
	}

	@Test
	fun `volunteer scan approves, the same key repeats it, a fresh key is already consumed`() {
		val secret = ByteArray(32) { 7 }
		val userId = newUser(secret)
		val windowId = newWindowWithPortion(userId, "meal.vegan")
		val token = ScanToken.build(userId, secret, Instant.now().epochSecond)
		val key = UUID.randomUUID()

		scan(MealScanRequestDto(token, windowId, key, ScanCarrier.QR), Role.VOLUNTEER).andExpect {
			status { isOk() }
			jsonPath("$.result") { value("APPROVED") }
			jsonPath("$.userId") { value(userId.toInt()) }
			jsonPath("$.variantKey") { value("meal.vegan") }
		}

		scan(MealScanRequestDto(token, windowId, key, ScanCarrier.QR), Role.VOLUNTEER).andExpect {
			status { isOk() }
			jsonPath("$.result") { value("APPROVED") }
			jsonPath("$.variantKey") { value("meal.vegan") }
		}

		scan(MealScanRequestDto(token, windowId, UUID.randomUUID(), ScanCarrier.QR), Role.VOLUNTEER).andExpect {
			status { isOk() }
			jsonPath("$.result") { value("ALREADY_CONSUMED") }
		}
	}

	@Test
	fun `visitor cannot scan`() {
		val secret = ByteArray(32) { 3 }
		val userId = newUser(secret)
		val windowId = newWindowWithPortion(userId, "meal.regular")
		val token = ScanToken.build(userId, secret, Instant.now().epochSecond)

		scan(MealScanRequestDto(token, windowId, UUID.randomUUID(), ScanCarrier.QR), Role.VISITOR).andExpect {
			status { isForbidden() }
		}
	}

	@Test
	fun `unreadable token is a verdict, not a transport error`() {
		val windowId = windows.save(newWindow()).id

		scan(MealScanRequestDto("Q1:nonsense", windowId, UUID.randomUUID(), ScanCarrier.QR), Role.VOLUNTEER).andExpect {
			status { isOk() }
			jsonPath("$.result") { value("NO_USER_FOUND") }
		}
	}

	@Test
	fun `a printed badge number feeds a flat phone`() {
		val userId = newUser(ByteArray(32) { 2 })
		val windowId = newWindowWithPortion(userId, "meal.regular")

		scan(MealScanRequestDto(userId.toString(), windowId, UUID.randomUUID(), ScanCarrier.BARCODE), Role.VOLUNTEER)
			.andExpect {
				status { isOk() }
				jsonPath("$.result") { value("APPROVED") }
				jsonPath("$.userId") { value(userId.toInt()) }
			}
	}

	/** A weak carrier label on a rotating token — or the reverse — is a client bug, caught loudly */
	@Test
	fun `a carrier that disagrees with the token is rejected`() {
		val secret = ByteArray(32) { 8 }
		val userId = newUser(secret)
		val windowId = newWindowWithPortion(userId, "meal.regular")
		val token = ScanToken.build(userId, secret, Instant.now().epochSecond)

		scan(MealScanRequestDto(token, windowId, UUID.randomUUID(), ScanCarrier.BARCODE), Role.VOLUNTEER).andExpect {
			status { isBadRequest() }
		}
	}

	/** Also guards the badge fallback: a forged token must not reach the bare-id branch */
	@Test
	fun `token signed by a foreign secret is not accepted`() {
		val userId = newUser(ByteArray(32) { 7 })
		val windowId = newWindowWithPortion(userId, "meal.vegan")
		val token = ScanToken.build(userId, ByteArray(32) { 9 }, Instant.now().epochSecond)

		scan(MealScanRequestDto(token, windowId, UUID.randomUUID(), ScanCarrier.QR), Role.VOLUNTEER).andExpect {
			status { isOk() }
			jsonPath("$.result") { value("NO_USER_FOUND") }
		}
	}

	@Test
	fun `scan without a reservation is not a registered portion`() {
		val secret = ByteArray(32) { 5 }
		val userId = newUser(secret)
		val windowId = windows.save(newWindow()).id
		val token = ScanToken.build(userId, secret, Instant.now().epochSecond)

		scan(MealScanRequestDto(token, windowId, UUID.randomUUID(), ScanCarrier.QR), Role.VOLUNTEER).andExpect {
			status { isOk() }
			jsonPath("$.result") { value("NOT_REGISTERED_PORTION") }
		}
	}

	private fun scan(request: MealScanRequestDto, role: Role) = mockMvc.post(ApiPaths.MEAL_SCAN) {
		with(
			jwt().jwt {
				it.subject(scannerSub.toString())
					.claim("realm_access", mapOf("roles" to listOf(role.name)))
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

	private fun newWindowWithPortion(userId: Long, variantKey: String): Long {
		val windowId = windows.save(newWindow()).id
		reservations.save(MealReservation(MealSlotId(userId, windowId), variantKey))
		return windowId
	}

	private fun newWindow() = MealWindow(0, "meal.test", Instant.now(), Instant.now().plusSeconds(3600))
}
