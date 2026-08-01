package tr.qonferencer.backend.admin

import java.security.SecureRandom

object UserPasswordGenerator {
	/** Crockford Base 32 alphabet */
	private const val ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"
	
	/** [length] random chars */
	fun generate(random: SecureRandom, length: Int = 8): String =
		(1..length).map { ALPHABET[random.nextInt(ALPHABET.length)] }.joinToString("")
}
