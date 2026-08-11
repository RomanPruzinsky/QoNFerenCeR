package tr.qonferencer.backend.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.shared.CustomDataType
import java.security.SecureRandom
import java.util.UUID

/** **ANCHOR** is local record carrying app-specific data, that are not stored in Keycloak */
@Service
class UserAnchorService(
	private val users: UserRepository,
	private val objectMapper: ObjectMapper,
) {
	/** Makes sure entry for this user exists */
	@Transactional
	fun ensure(kcSub: UUID, fullName: String, customData: CustomDataType = emptyMap()): User {
		users.insertIfAbsent(kcSub, newSecret(), fullName, objectMapper.writeValueAsString(customData))
		return users.findByKcSub(kcSub) ?: error("anchor upsert failed for $kcSub")
	}

// ////////////////// CREATION ////////////////////
// ////////////////////////////////////////////////
// //////////////// CUSTOM DATA ///////////////////

	/** Deserializes [User.customData] as map */
	@Suppress("UNCHECKED_CAST")
	fun customData(user: User): CustomDataType =
		runCatching { objectMapper.readValue(user.customData, Map::class.java) as CustomDataType }
			.getOrDefault(emptyMap())

	/** Replaces whole [User.customData] */
	fun storeCustomData(user: User, customData: CustomDataType) {
		user.customData = objectMapper.writeValueAsString(customData)
		users.save(user)
	}

// //////////////// CUSTOM DATA ///////////////////
// ////////////////////////////////////////////////
// ///////////////// QR SECRET ////////////////////

	/**
	 * Gives [user] fresh scan secret, so every token built from old one stops verifying
	 * @return new [User.mealSecretV]
	 */
	fun rotateSecret(user: User): Short {
		user.mealSecret = newSecret()
		user.mealSecretV++
		users.save(user)
		return user.mealSecretV
	}
	
	private val random = SecureRandom()
	private fun newSecret(): ByteArray = ByteArray(SECRET_LENGTH).also { random.nextBytes(it) }
	
	private companion object {
		/** Length of `mealSecret` in bytes */
		const val SECRET_LENGTH = 32
	}
}
