package tr.qonferencer.backend.meal

import org.springframework.stereotype.Service
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.scan.ScanToken
import java.time.Instant

/** Resolves a scan token to the user it identifies; the raw token is never logged */
@Service
class MealScanTokenService(
	private val users: UserRepository,
) {
	/** Verified `app_user.id`, or null when the token is malformed, stale or not signed by that user's secret */
	fun verify(token: String, now: Instant = Instant.now()): Long? {
		val parsed = ScanToken.parse(token) ?: return null
		val secret = users.findById(parsed.userId).orElse(null)?.qrSecret ?: return null
		return parsed.userId.takeIf { ScanToken.matches(parsed, secret, now.epochSecond) }
	}
}
