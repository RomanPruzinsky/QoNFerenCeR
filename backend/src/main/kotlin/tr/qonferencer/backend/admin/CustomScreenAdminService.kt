package tr.qonferencer.backend.admin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.admin.CustomScreenAdminService.Companion.bodyType
import tr.qonferencer.backend.common.notFound
import tr.qonferencer.backend.content.CustomScreen
import tr.qonferencer.backend.content.CustomScreenRepository
import tr.qonferencer.shared.dtos.CustomElement
import tr.qonferencer.shared.dtos.CustomScreenAdminDto
import tr.qonferencer.shared.enums.Role

/** Admin CRUD over custom screens */
@Service
class CustomScreenAdminService(
	private val screens: CustomScreenRepository,
	private val objectMapper: ObjectMapper,
) {
	fun list(): List<CustomScreenAdminDto> = screens.findAll().sortedBy { it.id }.map { it.toAdminDto() }

	@Transactional
	fun upsert(
		id: String,
		req: CustomScreenAdminDto,
	): CustomScreenAdminDto {
		if (!req.isStartingScreen) return screens.save(req.copy(id = id).toEntity()).toAdminDto()

		unsetOtherStartingScreens(except = id)
		return screens.save(req.copy(id = id, minRole = Role.ANONYM).toEntity()).toAdminDto()
	}

	/** Enforces at most one starting screen */
	private fun unsetOtherStartingScreens(except: String) {
		val others = screens.findAll().filter { it.id != except && it.isStartingScreen }
		others.forEach { it.isStartingScreen = false }
		screens.saveAll(others)
	}

	fun delete(id: String) {
		findScreenOrThrow(id)
		screens.deleteById(id)
	}

	private fun findScreenOrThrow(id: String): CustomScreen =
		screens.findById(id).orElseThrow { notFound("custom_screen $id doesn't exist") }

	private fun CustomScreen.toAdminDto() = CustomScreenAdminDto(
		id = id,
		titleKey = titleKey,
		icon = icon,
		minRole = minRole,
		isStartingScreen = isStartingScreen,
		body = objectMapper.readValue(body, bodyType),
	)

	private fun CustomScreenAdminDto.toEntity() = CustomScreen(
		id = id,
		titleKey = titleKey,
		minRole = minRole,
		icon = icon,
		isStartingScreen = isStartingScreen,
		body = body.toBodyJson(),
	)

	/** Serializes with static [bodyType] so Jackson keeps  type ids at root level */
	private fun List<CustomElement>.toBodyJson(): String = objectMapper.writerFor(bodyType).writeValueAsString(this)

	private companion object {
		private val bodyType = object : TypeReference<List<CustomElement>>() {}
	}
}
