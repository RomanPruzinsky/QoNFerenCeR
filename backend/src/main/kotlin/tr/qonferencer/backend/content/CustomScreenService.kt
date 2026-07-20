package tr.qonferencer.backend.content

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import tr.qonferencer.backend.common.forbidden
import tr.qonferencer.backend.common.notFound
import tr.qonferencer.backend.user.CallerService
import tr.qonferencer.shared.dtos.CustomElement

/** Serves a custom screen's body (its [CustomElement] tree), gated by [CustomScreen.minRole] */
@Service
class CustomScreenService(
	private val screens: CustomScreenRepository,
	private val caller: CallerService,
	private val objectMapper: ObjectMapper,
) {
	fun body(id: String): List<CustomElement> {
		val screen = screens.findById(id).orElseThrow { notFound("custom_screen $id does not exist") }
		if (!caller.activeRole().atLeast(screen.minRole)) throw forbidden("role below screen minRole")
		return objectMapper.readValue(screen.body, object : TypeReference<List<CustomElement>>() {})
	}
}
