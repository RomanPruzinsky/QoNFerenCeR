package tr.qonferencer.backend.meal

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

/**
 * Per-person meal reservation
 * @property id Composite key (user + window)
 * @property variantKey Translation key of the meal variant the user gets
 */
@Entity
@Table(name = "meal_reservation")
class MealReservation(
	
	@EmbeddedId
	var id: MealSlotId,
	
	@Column(name = "variant_key", nullable = false)
	var variantKey: String,
)
