package tr.qonferencer.shared.scan

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScanTokenTest {
	
	private val secret = ByteArray(32) { it.toByte() }
	private val foreignSecret = ByteArray(32) { (it + 1).toByte() }
	private val now = 1_780_000_000L
	
	@Test
	fun `round trip verifies`() {
		val parsed = assertNotNull(ScanToken.parse(ScanToken.build(42, secret, now)))
		assertEquals(42L, parsed.userId)
		assertTrue(ScanToken.matches(parsed, secret, now))
	}
	
	@Test
	fun `neighbouring windows verify, further ones do not`() {
		val parsed = assertNotNull(ScanToken.parse(ScanToken.build(42, secret, now)))
		val window = ScanToken.WINDOW_SECONDS
		assertTrue(ScanToken.matches(parsed, secret, now + window))
		assertTrue(ScanToken.matches(parsed, secret, now - window))
		assertFalse(ScanToken.matches(parsed, secret, now + 2 * window))
		assertFalse(ScanToken.matches(parsed, secret, now - 2 * window))
	}
	
	@Test
	fun `foreign secret doesn't verify`() {
		val parsed = assertNotNull(ScanToken.parse(ScanToken.build(42, secret, now)))
		assertFalse(ScanToken.matches(parsed, foreignSecret, now))
	}
	
	@Test
	fun `swapped userId doesn't verify`() {
		val parsed = assertNotNull(ScanToken.parse(ScanToken.build(42, secret, now)))
		assertFalse(ScanToken.matches(parsed.copy(userId = 43), secret, now))
	}
	
	@Test
	fun `malformed tokens parse to null`() {
		val hmac = assertNotNull(ScanToken.parse(ScanToken.build(42, secret, now))).hmac
		assertNull(ScanToken.parse(""))
		assertNull(ScanToken.parse("nonsense"))
		assertNull(ScanToken.parse("Q2:42:59333333:$hmac"))
		assertNull(ScanToken.parse("Q1:42:59333333"))
		assertNull(ScanToken.parse("Q1:abc:59333333:$hmac"))
		assertNull(ScanToken.parse("Q1:42:later:$hmac"))
	}
	
	@Test
	fun `tampered hmac fails to match`() {
		val parsed = assertNotNull(ScanToken.parse(ScanToken.build(42, secret, now)))
		assertFalse(ScanToken.matches(parsed.copy(hmac = parsed.hmac.drop(1)), secret, now))
		assertFalse(ScanToken.matches(parsed.copy(hmac = parsed.hmac.dropLast(1) + "0"), secret, now))
	}
}
