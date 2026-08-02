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

/** Outcome of [MealConsumptionRepository.consume] */
enum class ConsumeOutcome {
	/** First successful scan for this slot */
	NEW,

	/** Same [MealConsumption.idempotencyKey] seen before, valid */
	RETRY,

	/** Slot already consumed under a different [MealConsumption.idempotencyKey] */
	CONFLICT,
}

interface MealConsumptionRepository : JpaRepository<MealConsumption, MealSlotId> {
	
	fun deleteByIdUserId(userId: Long)

	/** Clears [userId] from `scannedBy` */
	@Modifying
	@Query(
		value = "UPDATE meal_consumption SET scanned_by = NULL WHERE scanned_by = :userId",
		nativeQuery = true,
	)
	fun detachScanner(@Param("userId") userId: Long): Int

	/** Records consumption */
	fun consume(slot: MealSlotId, scannedBy: Long?, idempotencyKey: UUID): ConsumeOutcome {
		if (insertIfAbsent(slot.userId, slot.windowId, scannedBy, idempotencyKey) == 1) return ConsumeOutcome.NEW
		val idempotencyKeysMatches = findById(slot).orElse(null)?.idempotencyKey == idempotencyKey
		return if (idempotencyKeysMatches) ConsumeOutcome.RETRY else ConsumeOutcome.CONFLICT
	}

	/**
	 * Inserts meal consumption entry. Tracks if inserted or skipped
	 * @return `1` on insert, `0` when slot already existed
	 * @see [consume]
	 */
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
