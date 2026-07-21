package tr.qonferencer.shared.scan

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs

/**
 * Rotating scan token `Q1:<userId>:<window>:<hmac>`, built by the phone and verified by the backend
 *
 * `hmac` is base32 of the first 10 bytes (80 bits) of `HMAC-SHA256(qrSecret, "<userId>:<window>")`.
 * One shared implementation keeps format and base32 alphabet identical on both sides.
 */
object ScanToken {

	/** Seconds one token stays current */
	const val WINDOW_SECONDS = 30L

	/** How many windows around the current one still verify (clock skew) */
	const val WINDOW_TOLERANCE = 1L

	private const val PREFIX = "Q1"
	private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
	private const val MAC_BYTES = 10
	private const val HMAC_LENGTH = 16

	/**
	 * Token split into its fields, nothing verified yet
	 * @property userId Claimed `app_user.id`
	 * @property window Claimed time window
	 * @property hmac Claimed signature, authentic only once [matches] says so
	 */
	data class Parsed(
		val userId: Long,
		val window: Long,
		val hmac: String,
	)

	/** Builds the token current at [epochSeconds] */
	fun build(userId: Long, qrSecret: ByteArray, epochSeconds: Long): String {
		val window = windowOf(epochSeconds)
		return "$PREFIX:$userId:$window:${hmacOf(userId, window, qrSecret)}"
	}

	/** Splits the token; null on a wrong prefix, shape or base32 body */
	fun parse(token: String): Parsed? {
		val parts = token.split(':')
		if (parts.size != 4 || parts[0] != PREFIX) return null
		val userId = parts[1].toLongOrNull() ?: return null
		val window = parts[2].toLongOrNull() ?: return null
		val hmac = parts[3]
		if (hmac.length != HMAC_LENGTH || hmac.any { it !in ALPHABET }) return null
		return Parsed(userId, window, hmac)
	}

	/** Whether [parsed] is authentic for [qrSecret] and still current at [epochSeconds]; compares constant-time */
	fun matches(parsed: Parsed, qrSecret: ByteArray, epochSeconds: Long): Boolean {
		if (abs(windowOf(epochSeconds) - parsed.window) > WINDOW_TOLERANCE) return false
		val expected = hmacOf(parsed.userId, parsed.window, qrSecret)
		return MessageDigest.isEqual(expected.toByteArray(), parsed.hmac.toByteArray())
	}

	private fun windowOf(epochSeconds: Long): Long = Math.floorDiv(epochSeconds, WINDOW_SECONDS)

	private fun hmacOf(userId: Long, window: Long, qrSecret: ByteArray): String {
		val mac = Mac.getInstance("HmacSHA256").apply { init(SecretKeySpec(qrSecret, "HmacSHA256")) }
		return base32(mac.doFinal("$userId:$window".toByteArray()).copyOf(MAC_BYTES))
	}

	/** Encodes [bytes] whose bit count is a multiple of 5, so no padding is ever needed */
	private fun base32(bytes: ByteArray): String {
		val out = StringBuilder(HMAC_LENGTH)
		var buffer = 0L
		var bits = 0
		for (byte in bytes) {
			buffer = (buffer shl 8) or (byte.toLong() and 0xFF)
			bits += 8
			while (bits >= 5) {
				bits -= 5
				out.append(ALPHABET[((buffer shr bits) and 0x1F).toInt()])
			}
		}
		return out.toString()
	}
}
