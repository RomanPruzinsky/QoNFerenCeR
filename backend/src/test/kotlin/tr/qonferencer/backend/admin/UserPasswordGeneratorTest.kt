package tr.qonferencer.backend.admin

import java.security.SecureRandom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserPasswordGeneratorTest {

	@Test
	fun `generates length-8 crockford without ambiguous chars`() {
		val random = SecureRandom()
		repeat(200) {
			val pwd = UserPasswordGenerator.generate(random)
			assertEquals(8, pwd.length)
			assertTrue(pwd.all { it in UserPasswordGenerator.ALPHABET })
			assertTrue(pwd.none { it in "ILOU" })
		}
	}
}
