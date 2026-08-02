package tr.qonferencer.backend.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.meal.MealConsumptionRepository
import tr.qonferencer.backend.meal.MealReservationRepository

@Service
class UserDeleteService(
	private val users: UserRepository,
	private val mealReservations: MealReservationRepository,
	private val mealConsumptions: MealConsumptionRepository,
) {
	/** Deletes data of [userId] and everything pointing at it */
	@Transactional
	fun delete(userId: Long) {
		mealConsumptions.detachScanner(userId)
		mealConsumptions.deleteByIdUserId(userId)
		mealReservations.deleteByIdUserId(userId)
		users.deleteById(userId)
	}
}
