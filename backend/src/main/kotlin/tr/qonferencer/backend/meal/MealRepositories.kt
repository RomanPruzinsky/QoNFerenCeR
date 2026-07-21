package tr.qonferencer.backend.meal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MealWindowRepository : JpaRepository<MealWindow, Long>

interface MealReservationRepository : JpaRepository<MealReservation, MealSlotId> {

	fun findByIdUserId(userId: Long): List<MealReservation>
}

interface MealConsumptionRepository : JpaRepository<MealConsumption, MealSlotId> {

	/** Records the consumption; false when the slot was already consumed */
	fun consume(userId: Long, windowId: Long, scannedBy: Long?): Boolean = insertIfAbsent(userId, windowId, scannedBy) == 1

	/** Backs [consume]; `@Modifying` allows only void/int/long as return type */
	@Modifying
	@Query(
		value = """
			INSERT INTO meal_consumption (user_id, window_id, scanned_by, scanned_at)
			VALUES (:userId, :windowId, :scannedBy, now())
			ON CONFLICT (user_id, window_id) DO NOTHING
		""",
		nativeQuery = true,
	)
	fun insertIfAbsent(
		@Param("userId") userId: Long,
		@Param("windowId") windowId: Long,
		@Param("scannedBy") scannedBy: Long?,
	): Int
}
