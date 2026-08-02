package tr.qonferencer.backend.admin

import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.TestcontainersConfiguration
import tr.qonferencer.backend.meal.MealConsumptionRepository
import tr.qonferencer.backend.meal.MealReservation
import tr.qonferencer.backend.meal.MealReservationRepository
import tr.qonferencer.backend.meal.MealSlotId
import tr.qonferencer.backend.meal.MealWindow
import tr.qonferencer.backend.meal.MealWindowRepository
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserMealEntryDto
import tr.qonferencer.shared.enums.Role
import java.time.Instant
import java.util.Base64
import java.util.UUID
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Update, revoke and erase, straight against the service with Keycloak mocked away */
@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class SlotLifecycleTest {
	
	@MockitoBean
	private lateinit var keycloak: KeycloakAdminService
	
	@Autowired
	private lateinit var slots: SlotService
	
	@Autowired
	private lateinit var users: UserRepository
	
	@Autowired
	private lateinit var windows: MealWindowRepository
	
	@Autowired
	private lateinit var reservations: MealReservationRepository
	
	@Autowired
	private lateinit var consumptions: MealConsumptionRepository
	
	@Test
	fun `update replaces the name and the whole meal plan`() {
		val userId = newUser("Jana Nováková")
		val lunch = newWindow()
		val dinner = newWindow()
		reservations.save(MealReservation(MealSlotId(userId, lunch), "meal.meat"))
		
		slots.updateUserSlot(
			userId,
			ModifyableUserDataDto(
				fullName = "Jana Kováčová",
				role = Role.VOLUNTEER,
				meals = listOf(UserMealEntryDto(dinner, "meal.vegan")),
			),
		)
		
		assertEquals("Jana Kováčová", users.findById(userId).orElseThrow().fullName)
		assertContentEquals(
			listOf(dinner to "meal.vegan"),
			reservations.findByIdUserId(userId).map { it.id.windowId to it.variantKey },
		)
	}
	
	@Test
	fun `issuing login also hands back the current scan secret, base64-encoded`() {
		val userId = newUser("Roman Pružinský")
		val user = users.findById(userId).orElseThrow()
		Mockito.`when`(keycloak.username(user.kcSub)).thenReturn("slot_007")
		
		val credentials = slots.getLoginCredentials(userId)
		
		assertEquals(Base64.getEncoder().encodeToString(user.qrSecret), credentials.qrSecret)
	}
	
	@Test
	fun `revoke rotates the scan secret and counts the rotation`() {
		val userId = newUser("Peter Novák")
		val before = users.findById(userId).orElseThrow().qrSecret.copyOf()
		
		slots.revokeDevice(userId)
		
		val after = users.findById(userId).orElseThrow()
		assertFalse(before.contentEquals(after.qrSecret), "secret must not survive a revoke")
		assertEquals(1, after.qrSecretV.toInt())
	}
	
	@Test
	fun `erasing an attendee leaves nothing of theirs behind`() {
		val userId = newUser("Marek Kovacs")
		val window = newWindow()
		reservations.save(MealReservation(MealSlotId(userId, window), "meal.vegan"))
		consumptions.consume(MealSlotId(userId, window), null, UUID.randomUUID())
		
		slots.deleteUserSlot(userId)
		
		assertTrue(users.findById(userId).isEmpty)
		assertTrue(reservations.findByIdUserId(userId).isEmpty())
		assertTrue(consumptions.findById(MealSlotId(userId, window)).isEmpty)
	}

	/** The volunteer leaving must not erase the meals other people were handed */
	@Test
	fun `erasing a volunteer keeps the meals they scanned, minus their name on them`() {
		val volunteerId = newUser("Volunteer Scanner")
		val dinerId = newUser("Hungry Attendee")
		val window = newWindow()
		consumptions.consume(MealSlotId(dinerId, window), volunteerId, UUID.randomUUID())
		
		slots.deleteUserSlot(volunteerId)
		
		val meal = consumptions.findById(MealSlotId(dinerId, window)).orElseThrow()
		assertNull(meal.scannedBy, "the scanner reference must be cleared, not cascade the row away")
	}
	
	private fun newUser(fullName: String): Long {
		val sub = UUID.randomUUID()
		users.insertIfAbsent(sub, ByteArray(32) { 1 }, fullName)
		return users.findByKcSub(sub)!!.id
	}
	
	private fun newWindow(): Long =
		windows.save(MealWindow(0, "meal.test", Instant.now(), Instant.now().plusSeconds(3600))).id
}
