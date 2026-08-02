package tr.qonferencer.backend.admin

import jakarta.persistence.EntityManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.common.badRequest
import tr.qonferencer.backend.common.notFound
import tr.qonferencer.backend.meal.MealReservation
import tr.qonferencer.backend.meal.MealReservationRepository
import tr.qonferencer.backend.meal.MealSlotId
import tr.qonferencer.backend.meal.MealWindowRepository
import tr.qonferencer.backend.n8n.OutboundEvent
import tr.qonferencer.backend.user.User
import tr.qonferencer.backend.user.UserAnchorService
import tr.qonferencer.backend.user.UserDeleteService
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.dtos.LoginCredentialsDto
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.SlotProvisionedDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.dtos.UserMealEntryDto
import java.security.SecureRandom
import java.util.Base64

/** Result of [SlotService.deleteUserSlot]: whether Keycloak user was erased along with app data */
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
	private val events: ApplicationEventPublisher,
) {
	private val random = SecureRandom()

	/** Provisions one attendee: Keycloak user + password, app anchor, custom data, meal reservations */
	@Transactional
	fun createUserSlot(req: ModifyableUserDataDto): SlotProvisionedDto {
		validateMealWindows(req.meals)
		val username = "slot_%03d".format(nextSlotNumber())
		val sub = kc.createUser(
			username = username,
			role = req.role,
			isSpeaker = req.isSpeaker,
			canCheckByName = req.canCheckByName,
		)
		val password = UserPasswordGenerator.generate(random)
		kc.setPassword(sub, password)
		val user = anchors.ensure(sub, req.fullName)
		anchors.storeCustomData(user, req.customData)
		req.meals.forEach {
			reservations.save(MealReservation(MealSlotId(user.id, it.windowId), it.variantKey))
		}
		events.publishEvent(OutboundEvent.SlotCreated(userId = user.id, username = username, userData = req))
		return SlotProvisionedDto(
			user = userDetail(user, req),
			credentials = loginCredentials(user, username, password),
		)
	}

	/** Replaces everything mutable about [userId]; replaces reservations, keeps consumptions */
	@Transactional
	fun updateUserSlot(userId: Long, req: ModifyableUserDataDto): UserDetailDto {
		validateMealWindows(req.meals)
		val user = findUser(userId)
		kc.updateUser(user.kcSub, req.role, req.isSpeaker, req.canCheckByName)
		user.fullName = req.fullName
		anchors.storeCustomData(user, req.customData)
		reservations.deleteByIdUserId(userId)
		req.meals.forEach {
			reservations.save(MealReservation(MealSlotId(userId, it.windowId), it.variantKey))
		}
		events.publishEvent(OutboundEvent.SlotUpdated(userId = userId, userData = req))
		return userDetail(user, req)
	}

	/** Cuts lost phone off: rotates scan secret and kills every Keycloak session */
	@Transactional
	fun revokeDevice(userId: Long) {
		val user = findUser(userId)
		val version = anchors.rotateSecret(user)
		kc.logout(user.kcSub)
		events.publishEvent(OutboundEvent.SlotRevoked(userId = user.id, qrSecretV = version))
	}

	/** Erases [userId] from app database first, then from Keycloak; not transactional */
	fun deleteUserSlot(userId: Long): DeleteOutcome {
		val sub = findUser(userId).kcSub
		userDelete.delete(userId)
		val outcome = runCatching { kc.deleteUser(sub) }
			.fold({ DeleteOutcome.FULL }, { DeleteOutcome.KEYCLOAK_SURVIVED })
		if (outcome == DeleteOutcome.KEYCLOAK_SURVIVED) {
			log.warn("app data for $userId is gone, Keycloak user survives")
		}
		events.publishEvent(OutboundEvent.SlotDeleted(userId = userId))
		return outcome
	}

	/** Re-issue fresh password for slot and return its login credentials, plus its scan secret */
	fun getLoginCredentials(userId: Long): LoginCredentialsDto {
		val user = findUser(userId)
		val password = UserPasswordGenerator.generate(random)
		kc.setPassword(user.kcSub, password)
		val username = kc.username(user.kcSub)
		events.publishEvent(OutboundEvent.SlotLoginIssued(userId = user.id, username = username))
		return loginCredentials(user, username, password)
	}
	
	private fun nextSlotNumber(): Long =
		(entityManager.createNativeQuery("SELECT nextval('slot_seq')").singleResult as Number).toLong()
	
	private fun findUser(userId: Long): User =
		users.findById(userId).orElseThrow { notFound("app_user $userId doesn't exist") }
	
	private fun validateMealWindows(meals: List<UserMealEntryDto>) = meals.forEach {
		if (!windows.existsById(it.windowId)) throw badRequest("meal window ${it.windowId} doesn't exist")
	}
	
	private fun loginCredentials(user: User, username: String, password: String) = LoginCredentialsDto(
		username = username,
		password = password,
		qrSecret = Base64.getEncoder().encodeToString(user.qrSecret),
	)
	
	private fun userDetail(user: User, mod: ModifyableUserDataDto) = UserDetailDto(
		userId = user.id,
		fullName = user.fullName,
		role = mod.role,
		isSpeaker = mod.isSpeaker,
		canCheckByName = mod.canCheckByName,
		meals = mod.meals,
		customData = mod.customData,
	)
	
	private companion object {
		val log: Logger = LoggerFactory.getLogger(SlotService::class.java)
	}
}
