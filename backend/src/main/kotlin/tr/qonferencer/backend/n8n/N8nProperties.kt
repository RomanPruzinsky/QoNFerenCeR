package tr.qonferencer.backend.n8n

import jakarta.validation.constraints.Pattern
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * Where outbound events go
 * @property enabled Whether outbound delivery is on
 * @property baseUrl n8n webhook path
 * @property pathPrefix Path part shared by every workflow
 * @property eventId Conference identity
 * @property authToken Shared secret sent on every request; blank omits the header
 * @property timeoutMs Connect and read timeout
 */
@Validated
@ConfigurationProperties(prefix = "qonferencer.n8n")
data class N8nProperties(
	val enabled: Boolean,
	@field:Pattern(regexp = "https?://.+")
	val baseUrl: String,
	@field:Pattern(regexp = "[a-zA-Z0-9_]+")
	val pathPrefix: String,
	@field:Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_]*")
	val eventId: String,
	val authToken: String,
	val timeoutMs: Long = 3000,
)
