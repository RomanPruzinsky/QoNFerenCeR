package tr.qonferencer.backend.admin

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.common.notFound
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.dtos.CreateSlotDto
import tr.qonferencer.shared.dtos.SlotCredentialsDto
import tr.qonferencer.shared.dtos.SlotDto
import java.security.SecureRandom

/**
 * Slot provisioning:
 * - Keycloak users
 * - pre-created app anchors
 * - login re-issue
 */
@Service
class SlotService(
	private val kc: KeycloakAdminService,
	private val users: UserRepository,
	private val entityManager: EntityManager,
	private val objectMapper: ObjectMapper,
) {
	private val random = SecureRandom()

	/** Provision one slot: KC user + anchor holding [req] customData (own tx, client loops for bulk) */
	@Transactional
	fun createSlot(req: CreateSlotDto): SlotDto {
		val username = "slot_%03d".format(nextSlotNumber())
		val sub = kc.createUser(username, req.role)
		users.insertIfAbsent(sub, newSecret())
		val user = users.findByKcSub(sub) ?: error("anchor upsert failed")
		user.customData = objectMapper.writeValueAsString(req.customData)
		users.save(user)
		return SlotDto(user.id, username, req.customData)
	}

	/** All app anchors, for organizer name lookup (customData holds imported data) */
	fun listSlots(): List<SlotDto> = users.findAll().map { SlotDto(it.id, customData = readMap(it.customData)) }

	/** Re-issue a fresh password for the slot and return its login credentials */
	fun issueLogin(userId: Long): SlotCredentialsDto {
		val user = users.findById(userId).orElseThrow { notFound("app_user $userId does not exist") }
		val password = SlotPasswords.generate(random)
		kc.setPassword(user.kcSub, password)
		return SlotCredentialsDto(kc.username(user.kcSub), password)
	}

	private fun nextSlotNumber(): Long =
		(entityManager.createNativeQuery("SELECT nextval('slot_seq')").singleResult as Number).toLong()

	private fun newSecret(): ByteArray = ByteArray(32).also { random.nextBytes(it) }

	@Suppress("UNCHECKED_CAST")
	private fun readMap(json: String): Map<String, Any?> =
		runCatching { objectMapper.readValue(json, Map::class.java) as Map<String, Any?> }.getOrDefault(emptyMap())
}
