package tr.qonferencer.backend.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.shared.dtos.MeDto
import java.security.SecureRandom
import java.util.Base64

@Service
class MeService(
	private val users: UserRepository,
	private val caller: CallerService,
	private val objectMapper: ObjectMapper,
) {
	private val random = SecureRandom()

	@Transactional
	fun me(): MeDto {
		val sub = caller.kcSub()
		users.insertIfAbsent(sub, newSecret())
		val user = users.findByKcSub(sub) ?: error("upsert failed")
		return MeDto(
			userId = user.id,
			role = caller.role(),
			isSpeaker = caller.isSpeaker(),
			consented = user.consented,
			qrSecret = Base64.getEncoder().encodeToString(user.qrSecret),
			customJson = readMap(user.customJson),
		)
	}

	@Transactional
	fun consent() {
		val user = caller.requireAppUser()
		user.consented = true
		users.save(user)
	}

	private fun newSecret(): ByteArray = ByteArray(32).also { random.nextBytes(it) }

	@Suppress("UNCHECKED_CAST")
	private fun readMap(json: String): Map<String, Any?> =
		runCatching { objectMapper.readValue(json, Map::class.java) as Map<String, Any?> }
			.getOrDefault(emptyMap())
}
