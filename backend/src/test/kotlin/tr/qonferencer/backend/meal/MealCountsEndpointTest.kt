package tr.qonferencer.backend.meal

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.TestcontainersConfiguration
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.MealCountDto
import tr.qonferencer.shared.enums.Role
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MealCountsEndpointTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Autowired
	private lateinit var windows: MealWindowRepository

	@Autowired
	private lateinit var reservations: MealReservationRepository

	@Autowired
	private lateinit var consumptions: MealConsumptionRepository

	@Autowired
	private lateinit var users: UserRepository

	@Test
	fun `remaining drops once consumed, unconsumed variants stay untouched`() {
		val windowId = windows.save(MealWindow(0, "meal.test", Instant.now(), Instant.now().plusSeconds(3600))).id

		val veganUser = newUser()
		val meatUser = newUser()
		reservations.save(MealReservation(MealSlotId(veganUser, windowId), "meal.vegan"))
		reservations.save(MealReservation(MealSlotId(meatUser, windowId), "meal.meat"))

		consumptions.consume(MealSlotId(veganUser, windowId), veganUser, UUID.randomUUID())

		val response = counts(windowId, Role.VOLUNTEER).andExpect { status { isOk() } }.andReturn().response
		val body: List<MealCountDto> = objectMapper.readValue(response.contentAsString)
		val remaining = body.associate { it.variantKey to it.remaining }

		assertEquals(0, remaining["meal.vegan"])
		assertEquals(1, remaining["meal.meat"])
	}

	@Test
	fun `visitor cannot see counts`() {
		val windowId = windows.save(MealWindow(0, "meal.test", Instant.now(), Instant.now().plusSeconds(3600))).id

		counts(windowId, Role.VISITOR).andExpect {
			status { isForbidden() }
		}
	}

	@Test
	fun `volunteer without the grant is refused`() {
		val windowId = windows.save(MealWindow(0, "meal.test", Instant.now(), Instant.now().plusSeconds(3600))).id

		counts(windowId, Role.VOLUNTEER, canFoodCheck = false).andExpect {
			status { isForbidden() }
		}
	}

	private fun newUser(): Long {
		val sub = UUID.randomUUID()
		users.insertIfAbsent(sub, ByteArray(32), "Hungry Attendee")
		return users.findByKcSub(sub)!!.id
	}

	private fun counts(
		windowId: Long,
		role: Role,
		canFoodCheck: Boolean = true,
	) = mockMvc.get(ApiPaths.Meal.MEAL_COUNTS.replace("{windowId}", windowId.toString())) {
		with(
			jwt().jwt {
				it.subject(UUID.randomUUID().toString())
					.claim("realm_access", mapOf("roles" to listOf(role.name)))
					.claim("canFoodCheck", canFoodCheck)
			},
		)
	}
}
