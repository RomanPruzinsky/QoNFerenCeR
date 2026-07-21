package tr.qonferencer.backend.meal

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

/**
 * Meal consumption record (scan); presence = consumed, insert-once
 * @property id Composite key (user + window)
 * @property scannedBy Volunteer who scanned (null if unknown)
 * @property scannedAt When it was scanned
 * @property idempotencyKey Scanner's key for this scan; the same key again is a retry, not a 2nd meal
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
