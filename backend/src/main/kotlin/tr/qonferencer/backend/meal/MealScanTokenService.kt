package tr.qonferencer.backend.meal

import org.springframework.stereotype.Service
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.scan.ScanToken
import java.time.Instant

/**
 * A verified scan: who it names, and how it was proven
 * @property userId Verified `app_user.id`
 * @property rotating True when a signature was checked, false when only the id's existence was
 */
data class VerifiedScan(
	val userId: Long,
	val rotating: Boolean,
)

/** Resolves what the scanner read to the person it identifies; the raw token is never logged */
@Service
class MealScanTokenService(
	private val users: UserRepository,
) {
	/** Who the scanner is looking at, or null; a token-shaped input is judged only as a token */
	fun verify(token: String, now: Instant = Instant.now()): VerifiedScan? {
		val parsed = ScanToken.parse(token)
		if (parsed != null) {
			val secret = users.findById(parsed.userId).orElse(null)?.qrSecret ?: return null
			if (!ScanToken.matches(parsed, secret, now.epochSecond)) return null
			return VerifiedScan(parsed.userId, rotating = true)
		}
		val userId = token.trim().toLongOrNull() ?: return null
		return if (users.existsById(userId)) VerifiedScan(userId, rotating = false) else null
	}
}
