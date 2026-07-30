package tr.qonferencer.backend.meal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface MealWindowRepository : JpaRepository<MealWindow, Long>

interface MealReservationRepository : JpaRepository<MealReservation, MealSlotId> {

	fun findByIdUserId(userId: Long): List<MealReservation>

	fun deleteByIdUserId(userId: Long)
}

interface MealConsumptionRepository : JpaRepository<MealConsumption, MealSlotId> {

	fun deleteByIdUserId(userId: Long)

	/** Clears [userId] from `scannedBy` without deleting the rows, which belong to other people */
	@Modifying
	@Query(
		value = "UPDATE meal_consumption SET scanned_by = NULL WHERE scanned_by = :userId",
		nativeQuery = true,
	)
	fun detachScanner(@Param("userId") userId: Long): Int

	/** Records the consumption; false when the slot was already consumed */
	fun consume(slot: MealSlotId, scannedBy: Long?, idempotencyKey: UUID): Boolean =
		insertIfAbsent(slot.userId, slot.windowId, scannedBy, idempotencyKey) == 1

	/** Backs [consume]; `@Modifying` allows only void/int/long as return type */
	@Modifying
	@Query(
		value = """
			INSERT INTO meal_consumption (user_id, window_id, scanned_by, scanned_at, idempotency_key)
			VALUES (:userId, :windowId, :scannedBy, now(), :idempotencyKey)
			ON CONFLICT (user_id, window_id) DO NOTHING
		""",
		nativeQuery = true,
	)
	fun insertIfAbsent(
		@Param("userId") userId: Long,
		@Param("windowId") windowId: Long,
		@Param("scannedBy") scannedBy: Long?,
		@Param("idempotencyKey") idempotencyKey: UUID,
	): Int
}
