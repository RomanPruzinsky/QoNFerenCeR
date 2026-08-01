package tr.qonferencer.backend.admin

import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
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
import tr.qonferencer.shared.dtos.LoginCredentialsDto
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserDetailDto
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
	fun createUserSlot(req: ModifyableUserDataDto): UserDetailDto {
		req.meals.forEach {
			if (!windows.existsById(it.windowId)) throw badRequest("meal window ${it.windowId} doesn't exist")
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
		return UserDetailDto(
			user.id,
			user.fullName,
			req.role,
			req.isSpeaker,
			req.canCheckByName,
			req.meals,
			req.customData,
		)
	}

	/** Replaces everything mutable about [userId]; replaces reservations, keeps consumptions */
	@Transactional
	fun updateUserSlot(userId: Long, req: ModifyableUserDataDto): UserDetailDto {
		req.meals.forEach {
			if (!windows.existsById(it.windowId)) throw badRequest("meal window ${it.windowId} doesn't exist")
		}
		val user = users.findById(userId).orElseThrow { notFound("app_user $userId doesn't exist") }
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
		return UserDetailDto(
			user.id,
			user.fullName,
			req.role,
			req.isSpeaker,
			req.canCheckByName,
			req.meals,
			req.customData,
		)
	}

	/** Cuts a lost phone off: rotates the scan secret and kills every Keycloak session */
	@Transactional
	fun revokeDevice(userId: Long) {
		val user = users.findById(userId).orElseThrow { notFound("app_user $userId doesn't exist") }
		val version = anchors.rotateSecret(user)
		kc.logout(user.kcSub)
		events.publish(
			EventType.SLOT_REVOKED,
			mapOf("userId" to user.id, "fullName" to user.fullName, "qrSecretV" to version),
		)
	}

	/** Erases [userId] from the app database first, then from Keycloak; not transactional */
	fun deleteUserSlot(userId: Long): DeleteOutcome {
		val user = users.findById(userId).orElseThrow { notFound("app_user $userId doesn't exist") }
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
	fun issueLogin(userId: Long): LoginCredentialsDto {
		val user = users.findById(userId).orElseThrow { notFound("app_user $userId doesn't exist") }
		val password = SlotPasswords.generate(random)
		kc.setPassword(user.kcSub, password)
		val username = kc.username(user.kcSub)
		events.publish(EventType.SLOT_LOGIN_ISSUED, mapOf("userId" to user.id, "username" to username))
		return LoginCredentialsDto(username, password, Base64.getEncoder().encodeToString(user.qrSecret))
	}
	
	private fun nextSlotNumber(): Long =
		(entityManager.createNativeQuery("SELECT nextval('slot_seq')").singleResult as Number).toLong()
	
	private companion object {
		val log = LoggerFactory.getLogger(SlotService::class.java)
	}
}
