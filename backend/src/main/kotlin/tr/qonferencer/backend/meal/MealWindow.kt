package tr.qonferencer.backend.meal

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

/**
 * Meal serving window
 * @property id Window id
 * @property nameKey Translation key of window name (breakfast, ...)
 * @property startsAt Serving start
 * @property endsAt Serving end
 */
@Entity
@Table(name = "meal_window")
class MealWindow(
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long,
	
	@Column(name = "name_key", nullable = false)
	var nameKey: String,
	
	@Column(name = "starts_at", nullable = false)
	var startsAt: Instant,
	
	@Column(name = "ends_at", nullable = false)
	var endsAt: Instant,
)
