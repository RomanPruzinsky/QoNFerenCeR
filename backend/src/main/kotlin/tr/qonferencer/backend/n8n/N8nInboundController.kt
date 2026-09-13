package tr.qonferencer.backend.n8n

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import tr.qonferencer.backend.admin.SlotService
import tr.qonferencer.backend.common.unauthorized
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.SlotProvisionedDto
import java.security.MessageDigest

/** Callbacks from n8n workflows, guarded by shared [N8nProperties.authToken] instead of Keycloak JWT */
@RestController
@ConditionalOnProperty(name = [N8nProperties.ENABLED_PROPERTY], havingValue = N8nProperties.ENABLED_VALUE)
class N8nInboundController(
	private val properties: N8nProperties,
	private val slotService: SlotService,
) {
	@PostMapping(ApiPaths.Internal.N8n.PREREGISTER_USER)
	@ResponseStatus(HttpStatus.CREATED)
	fun preregister(
		@RequestHeader(N8nOutboundListener.TOKEN_HEADER) token: String,
		@RequestBody req: ModifyableUserDataDto,
	): SlotProvisionedDto {
		requireValidToken(token)
		return slotService.createUserSlot(req)
	}
	
	private fun requireValidToken(token: String) {
		val current = token.toByteArray()
		val expected = properties.authToken.toByteArray()
		if (!MessageDigest.isEqual(current, expected)) throw unauthorized("invalid n8n token")
	}
}
