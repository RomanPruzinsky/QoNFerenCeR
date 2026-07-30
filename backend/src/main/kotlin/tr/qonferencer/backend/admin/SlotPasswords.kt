package tr.qonferencer.backend.admin

import java.security.SecureRandom

/** Crockford Base32 password generator */
object SlotPasswords {
	private const val ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"

	/** [length] random Crockford chars */
	fun generate(random: SecureRandom, length: Int = 8): String =
		(1..length).map { ALPHABET[random.nextInt(ALPHABET.length)] }.joinToString("")
}
