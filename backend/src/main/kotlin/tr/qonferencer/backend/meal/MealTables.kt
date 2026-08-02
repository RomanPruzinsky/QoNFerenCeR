package tr.qonferencer.backend.meal

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.io.Serializable
import java.time.Instant
import java.util.UUID

/** Composite key for [MealReservation] and [MealConsumption] */
@Embeddable
data class MealSlotId(
	@Column(name = "user_id")
	var userId: Long,
	@Column(name = "window_id")
	var windowId: Long,
) : Serializable

/**
 * Per-person meal reservation
 * @property id Composite key (user + window)
 * @property variantKey Translation key of meal variant user gets
 */
@Entity
@Table(name = "meal_reservation")
class MealReservation(
	
	@EmbeddedId
	var id: MealSlotId,
	
	@Column(name = "variant_key", nullable = false)
	var variantKey: String,
)

/**
 * Meal consumption record
 * @property id Composite key (user + window)
 * @property scannedBy Volunteer who scanned (null if unknown)
 * @property scannedAt When it was scanned
 * @property idempotencyKey Scanner's key for this scan
 */
@Entity
@Table(name = "meal_consumption")
class MealConsumption(
	
	@EmbeddedId
	var id: MealSlotId,
	
	@Column(name = "scanned_by")
	var scannedBy: Long?,
	
	@Column(name = "scanned_at", nullable = false)
	var scannedAt: Instant,
	
	@Column(name = "idempotency_key", nullable = false)
	var idempotencyKey: UUID,
)
