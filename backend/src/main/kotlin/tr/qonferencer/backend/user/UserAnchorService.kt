package tr.qonferencer.backend.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import tr.qonferencer.shared.CustomDataType
import java.security.SecureRandom
import java.util.UUID

// TODO: explain anchor
/** Sole owner of the app anchor's own fields: its `qrSecret` and its free-form `customData` */
@Service
class UserAnchorService(
	private val users: UserRepository,
	private val objectMapper: ObjectMapper,
) {
	private val random = SecureRandom()

	/** Makes sure entry for this user exists */
	fun ensure(kcSub: UUID, fullName: String): User {
		users.insertIfAbsent(kcSub, newSecret(), fullName)
		return users.findByKcSub(kcSub) ?: error("anchor upsert failed for $kcSub")
	}

	/** [User.customData] as a map; an unreadable bag reads as empty */
	@Suppress("UNCHECKED_CAST")
	fun customData(user: User): CustomDataType =
		runCatching { objectMapper.readValue(user.customData, Map::class.java) as CustomDataType }
			.getOrDefault(emptyMap())

	/** Replaces the whole [User.customData] bag and persists it */
	fun storeCustomData(user: User, customData: CustomDataType) {
		user.customData = objectMapper.writeValueAsString(customData)
		users.save(user)
	}

	/**
	 * Gives [user] a fresh scan secret, so every token built from the old one stops verifying
	 * @return the new [User.qrSecretV]
	 */
	fun rotateSecret(user: User): Short {
		user.qrSecret = newSecret()
		user.qrSecretV = (user.qrSecretV + 1).toShort()
		users.save(user)
		return user.qrSecretV
	}
	
	private fun newSecret(): ByteArray = ByteArray(SECRET_LENGTH).also { random.nextBytes(it) }
	
	private companion object {
		/** Length of `qrSecret` in bytes, matching the HMAC-SHA256 block the scan token signs with */
		const val SECRET_LENGTH = 32
	}
}
