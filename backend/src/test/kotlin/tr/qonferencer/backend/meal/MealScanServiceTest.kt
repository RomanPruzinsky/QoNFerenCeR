package tr.qonferencer.backend.meal

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.TestcontainersConfiguration
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.enums.MealScanResult
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class MealScanServiceTest {

	@Autowired
	private lateinit var scanService: MealScanService

	@Autowired
	private lateinit var windows: MealWindowRepository

	@Autowired
	private lateinit var reservations: MealReservationRepository

	@Autowired
	private lateinit var users: UserRepository

	@Test
	@Transactional
	fun `first scan approves, second is already consumed, unregistered window is rejected`() {
		val userId = newUser()
		val window = windows.save(newWindow())
		reservations.save(MealReservation(MealSlotId(userId, window.id), "meal.vegan"))

		val first = scanService.scan(userId, window.id, null)
		assertEquals(MealScanResult.APPROVED, first.result)
		assertEquals("meal.vegan", first.variantKey)

		val second = scanService.scan(userId, window.id, null)
		assertEquals(MealScanResult.ALREADY_CONSUMED, second.result)

		val unregistered = windows.save(newWindow())
		val third = scanService.scan(userId, unregistered.id, null)
		assertEquals(MealScanResult.NOT_REGISTERED_PORTION, third.result)
	}

	private fun newUser(): Long {
		val sub = UUID.randomUUID()
		users.insertIfAbsent(sub, ByteArray(32))
		return users.findByKcSub(sub)!!.id
	}

	private fun newWindow() = MealWindow(0, "meal.test", Instant.now(), Instant.now().plusSeconds(3600))
}
