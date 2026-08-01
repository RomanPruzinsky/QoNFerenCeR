package tr.qonferencer.backend.n8n

import com.fasterxml.jackson.annotation.JsonIgnore
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.dtos.UserMealEntryDto
import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.ScannerType

/**
 * Loggable actions for n8n tracking 
 * @property type Webhook path segment
 */
sealed class OutboundEvent(
	@get:JsonIgnore val type: String,
) {
	/**
	 * Somebody opened the app
	 * @return User data or `null` if user wasn't logged in 
	 */
	data class AppLaunched(
		val user: UserDetailDto?,
	) : OutboundEvent("APP_LAUNCHED")

	/** A slot was provisioned (Keycloak user + app anchor + first login) */
	data class SlotCreated(
		val userId: Long,
		val username: String,
		val user: ModifyableUserDataDto,
	) : OutboundEvent("SLOT_CREATED")

	/** An attendee's details, role or meals changed */
	data class SlotUpdated(
		val userId: Long,
		val user: ModifyableUserDataDto,
	) : OutboundEvent("SLOT_UPDATED")

	/** A lost phone was cut off: scan secret rotated and sessions killed */
	data class SlotRevoked(
		val userId: Long,
		val fullName: String,
		val qrSecretV: Short,
	) : OutboundEvent("SLOT_REVOKED")

	/** An attendee was erased; nothing of theirs survives to be reported later */
	data class SlotDeleted(
		val userId: Long,
	) : OutboundEvent("SLOT_DELETED")

	/** A meal was handed out */
	data class MealApproved(
		val userId: Long,
		val meal: UserMealEntryDto,
		val scannedBy: Long,
		val scannerType: ScannerType,
	) : OutboundEvent("MEAL_APPROVED")

	/** A scan was refused; [reason] carries which verdict */
	data class MealDenied(
		val userId: Long?,
		val windowId: Long,
		val reason: MealScanResult,
		val scannedBy: Long,
		val scannerType: ScannerType,
	) : OutboundEvent("MEAL_DENIED")
}
