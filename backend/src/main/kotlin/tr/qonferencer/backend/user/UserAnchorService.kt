package tr.qonferencer.backend.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.UUID

/** Sole owner of the app anchor's own fields: its `qrSecret` and its free-form `customData` */
@Service
class UserAnchorService(
	private val users: UserRepository,
	private val objectMapper: ObjectMapper,
) {
	private val random = SecureRandom()

	/** The anchor of [kcSub] named [fullName], created with a fresh secret when it has none yet */
	fun ensure(kcSub: UUID, fullName: String): User {
		users.insertIfAbsent(kcSub, newSecret(), fullName)
		return users.findByKcSub(kcSub) ?: error("anchor upsert failed for $kcSub")
	}

	/** [User.customData] as a map; an unreadable bag reads as empty */
	@Suppress("UNCHECKED_CAST")
	fun customData(user: User): Map<String, Any?> =
		runCatching { objectMapper.readValue(user.customData, Map::class.java) as Map<String, Any?> }
			.getOrDefault(emptyMap())

	/** Replaces the whole [User.customData] bag and persists it */
	fun storeCustomData(user: User, customData: Map<String, Any?>) {
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
