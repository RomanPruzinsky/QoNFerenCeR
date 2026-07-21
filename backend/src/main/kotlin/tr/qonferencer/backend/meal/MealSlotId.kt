package tr.qonferencer.backend.meal

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

/** Composite key (user + window) shared by [MealReservation] and [MealConsumption] */
@Embeddable
data class MealSlotId(
	@Column(name = "user_id")
	var userId: Long,
	@Column(name = "window_id")
	var windowId: Long,
) : Serializable
