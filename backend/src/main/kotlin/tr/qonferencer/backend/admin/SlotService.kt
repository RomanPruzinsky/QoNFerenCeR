package tr.qonferencer.backend.admin

import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.common.badRequest
import tr.qonferencer.backend.common.notFound
import tr.qonferencer.backend.meal.MealReservation
import tr.qonferencer.backend.meal.MealReservationRepository
import tr.qonferencer.backend.meal.MealSlotId
import tr.qonferencer.backend.meal.MealWindowRepository
import tr.qonferencer.backend.n8n.EventType
import tr.qonferencer.backend.n8n.OutboundEvents
import tr.qonferencer.backend.user.UserAnchorService
import tr.qonferencer.backend.user.UserDeleteService
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.dtos.CreateUserSlotDto
import tr.qonferencer.shared.dtos.SlotCredentialsDto
import tr.qonferencer.shared.dtos.SlotDto
import tr.qonferencer.shared.dtos.UpdateUserSlotDto
import java.security.SecureRandom
import java.util.Base64

/** Result of [SlotService.deleteUserSlot]: whether the Keycloak user was erased along with the app data */
enum class DeleteOutcome { FULL, KEYCLOAK_SURVIVED }

/** Slot provisioning: Keycloak users, app anchors, meal reservations, login re-issue */
@Service
class SlotService(
	private val kc: KeycloakAdminService,
	private val users: UserRepository,
	private val anchors: UserAnchorService,
	private val windows: MealWindowRepository,
	private val reservations: MealReservationRepository,
	private val userDelete: UserDeleteService,
	private val entityManager: EntityManager,
	private val events: OutboundEvents,
) {
	private val random = SecureRandom()

	/** Provisions one attendee: Keycloak user, app anchor, custom data and meal reservations */
	@Transactional
	fun createUserSlot(req: CreateUserSlotDto): SlotDto {
		req.meals.forEach {
			if (!windows.existsById(it.windowId)) throw badRequest("meal window ${it.windowId} does not exist")
		}
		val username = "slot_%03d".format(nextSlotNumber())
		val sub = kc.createUser(
			username = username,
			role = req.role,
			isSpeaker = req.isSpeaker,
			canCheckByName = req.canCheckByName,
		)
		val user = anchors.ensure(sub, req.fullName)
		anchors.storeCustomData(user, req.customData)
		req.meals.forEach {
			reservations.save(MealReservation(MealSlotId(user.id, it.windowId), it.variantKey))
		}
		events.publish(
			EventType.SLOT_CREATED,
			mapOf(
				"userId" to user.id,
				"username" to username,
				"fullName" to user.fullName,
				"role" to req.role.name,
				"isSpeaker" to req.isSpeaker,
				"customData" to req.customData,
				"meals" to req.meals.map { mapOf("windowId" to it.windowId, "variantKey" to it.variantKey) },
			),
		)
		return SlotDto(user.id, user.fullName, username, req.customData)
	}

	/** All app anchors, for organizer name lookup (customData holds imported data) */
	fun listSlots(pageable: Pageable): Page<SlotDto> =
		users.findAll(pageable).map { SlotDto(it.id, it.fullName, customData = anchors.customData(it)) }

	/** Replaces everything mutable about [userId]; replaces reservations, keeps consumptions */
	@Transactional
	fun updateUserSlot(userId: Long, req: UpdateUserSlotDto): SlotDto {
		req.meals.forEach {
			if (!windows.existsById(it.windowId)) throw badRequest("meal window ${it.windowId} does not exist")
		}
		val user = users.findById(userId).orElseThrow { notFound("app_user $userId does not exist") }
		kc.updateUser(user.kcSub, req.role, req.isSpeaker, req.canCheckByName)
		user.fullName = req.fullName
		anchors.storeCustomData(user, req.customData)
		reservations.deleteByIdUserId(userId)
		req.meals.forEach {
			reservations.save(MealReservation(MealSlotId(userId, it.windowId), it.variantKey))
		}
		events.publish(
			EventType.SLOT_UPDATED,
			mapOf(
				"userId" to userId,
				"fullName" to req.fullName,
				"role" to req.role.name,
				"isSpeaker" to req.isSpeaker,
				"customData" to req.customData,
				"meals" to req.meals.map { mapOf("windowId" to it.windowId, "variantKey" to it.variantKey) },
			),
		)
		return SlotDto(user.id, user.fullName, kc.username(user.kcSub), req.customData)
	}

	/** Cuts a lost phone off: rotates the scan secret and kills every Keycloak session */
	@Transactional
	fun revokeDevice(userId: Long): SlotDto {
		val user = users.findById(userId).orElseThrow { notFound("app_user $userId does not exist") }
		val version = anchors.rotateSecret(user)
		kc.logout(user.kcSub)
		events.publish(
			EventType.SLOT_REVOKED,
			mapOf("userId" to user.id, "fullName" to user.fullName, "qrSecretV" to version),
		)
		return SlotDto(user.id, user.fullName, customData = anchors.customData(user))
	}

	/** Erases [userId] from the app database first, then from Keycloak; not transactional */
	fun deleteUserSlot(userId: Long): DeleteOutcome {
		val user = users.findById(userId).orElseThrow { notFound("app_user $userId does not exist") }
		val sub = user.kcSub
		userDelete.delete(userId)
		val outcome = runCatching { kc.deleteUser(sub) }
			.fold({ DeleteOutcome.FULL }, { DeleteOutcome.KEYCLOAK_SURVIVED })
		if (outcome == DeleteOutcome.KEYCLOAK_SURVIVED) {
			log.warn("app data for {} is gone, Keycloak user survives", userId)
		}
		events.publish(EventType.SLOT_DELETED, mapOf("userId" to userId))
		return outcome
	}

	/** Re-issue a fresh password for the slot and return its login credentials, plus its scan secret */
	fun issueLogin(userId: Long): SlotCredentialsDto {
		val user = users.findById(userId).orElseThrow { notFound("app_user $userId does not exist") }
		val password = SlotPasswords.generate(random)
		kc.setPassword(user.kcSub, password)
		val username = kc.username(user.kcSub)
		events.publish(EventType.SLOT_LOGIN_ISSUED, mapOf("userId" to user.id, "username" to username))
		return SlotCredentialsDto(username, password, Base64.getEncoder().encodeToString(user.qrSecret))
	}

	private fun nextSlotNumber(): Long =
		(entityManager.createNativeQuery("SELECT nextval('slot_seq')").singleResult as Number).toLong()

	private companion object {
		val log = LoggerFactory.getLogger(SlotService::class.java)
	}
}
