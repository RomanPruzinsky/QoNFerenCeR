package tr.qonferencer.shared.scan

import java.security.MessageDigest
import java.util.HexFormat
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs

/** Rotating token `Q1:<window>:<userId>:<hmac>` */
object ScanToken {
	
	/** Token rotation time */
	const val WINDOW_SECONDS = 30L

	/** ±N valid windows */
	const val WINDOW_TOLERANCE = 1L

	/** Compatibility version of token */
	private const val TOKEN_VERSION = 1
	private const val PREFIX = "Q$TOKEN_VERSION"
	private const val HMAC_ALGORITHM = "HmacSHA256"

	/** Data obtained from token */
	data class Parsed(
		val userId: Long,
		val window: Long,
		val hmac: String,
	)

	/** Builds a fresh token for [userId], authenticated with [qrSecret], for the window containing [epochSeconds] */
	fun build(userId: Long, qrSecret: ByteArray, epochSeconds: Long): String {
		val window = windowOf(epochSeconds)
		return "$PREFIX:$window:$userId:${hmacOf(userId, window, qrSecret)}"
	}

	/**
	 * Splits [token] into its parts, without checking authenticity
	 * @return null if [token]'s shape doesn't match `Q1:<window>:<userId>:<hmac>`
	 */
	fun parse(token: String): Parsed? {
		val parts = token.split(':')
		if (parts.size != 4 || parts[0] != PREFIX) return null

		val (_, windowPart, userIdPart, hmac) = parts
		val window = windowPart.toLongOrNull() ?: return null
		val userId = userIdPart.toLongOrNull() ?: return null

		return Parsed(userId, window, hmac)
	}

	/** Whether [parsed] is authentic for [qrSecret] and still within [WINDOW_TOLERANCE] of [epochSeconds] */
	fun matches(parsed: Parsed, qrSecret: ByteArray, epochSeconds: Long): Boolean {
		if (abs(windowOf(epochSeconds) - parsed.window) > WINDOW_TOLERANCE) return false
		val expected = hmacOf(parsed.userId, parsed.window, qrSecret)
		return MessageDigest.isEqual(expected.toByteArray(), parsed.hmac.toByteArray())
	}
	
	private fun windowOf(epochSeconds: Long): Long = epochSeconds.floorDiv(WINDOW_SECONDS)
	
	private fun hmacOf(userId: Long, window: Long, qrSecret: ByteArray): String {
		val mac = Mac.getInstance(HMAC_ALGORITHM).apply { init(SecretKeySpec(qrSecret, HMAC_ALGORITHM)) }
		val digest = mac.doFinal("$window:$userId".toByteArray(Charsets.UTF_8))
		return HexFormat.of().formatHex(digest)
	}
}
