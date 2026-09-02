package tr.qonferencer.backend.n8n

import com.fasterxml.jackson.annotation.JsonIgnore
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.dtos.UserMealEntryDto
import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.ScannerType

/**
 * Loggable actions for n8n tracking 
 * @property type Webhook path part
 */
sealed class OutboundEvent(
	@get:JsonIgnore val type: String,
) {
	/**
	 * Somebody opened app
	 * @param user Caller data or `null` if user wasn't logged in 
	 */
	data class AppLaunched(
		val user: UserDetailDto?,
	) : OutboundEvent("APP_LAUNCHED")

	/** Slot created */
	data class SlotCreated(
		val userId: Long,
		val username: String,
		val userData: ModifyableUserDataDto,
	) : OutboundEvent("SLOT_CREATED")

	/** Fresh password issued for slot */
	data class SlotLoginIssued(
		val userId: Long,
		val username: String,
	) : OutboundEvent("SLOT_LOGIN_ISSUED")

	/** Attendee's details, role or meals changed */
	data class SlotUpdated(
		val userId: Long,
		val userData: ModifyableUserDataDto,
	) : OutboundEvent("SLOT_UPDATED")

	/** Scan secret rotated and sessions killed */
	data class SlotRevoked(
		val userId: Long,
		val mealSecretV: Short,
	) : OutboundEvent("SLOT_REVOKED")

	/** User erased */
	data class SlotDeleted(
		val userId: Long,
	) : OutboundEvent("SLOT_DELETED")

	/** Meal eaten */
	data class MealApproved(
		val userId: Long,
		val meal: UserMealEntryDto,
		val scannedBy: Long,
		val scannerType: ScannerType,
	) : OutboundEvent("MEAL_APPROVED")

	/** Scan refused, [reason] carries why */
	data class MealDenied(
		val userId: Long?,
		val windowId: Long,
		val reason: MealScanResult,
		val scannedBy: Long,
		val scannerType: ScannerType,
	) : OutboundEvent("MEAL_DENIED")
}
