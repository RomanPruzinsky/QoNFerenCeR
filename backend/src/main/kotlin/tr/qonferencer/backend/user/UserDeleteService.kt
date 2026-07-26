package tr.qonferencer.backend.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.meal.MealConsumptionRepository
import tr.qonferencer.backend.meal.MealReservationRepository

/** Erases everything one person left in the app database; own bean so `@Transactional` applies */
@Service
class UserDeleteService(
	private val users: UserRepository,
	private val reservations: MealReservationRepository,
	private val consumptions: MealConsumptionRepository,
) {
	/** Deletes the anchor of [userId] and everything pointing at it, in foreign-key order */
	@Transactional
	fun delete(userId: Long) {
		consumptions.detachScanner(userId)
		consumptions.deleteByIdUserId(userId)
		reservations.deleteByIdUserId(userId)
		users.deleteById(userId)
	}
}
