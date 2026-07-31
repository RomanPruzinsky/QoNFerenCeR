package tr.qonferencer.backend.n8n

import jakarta.validation.constraints.Pattern
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * Where outbound events go
 * @property enabled False means a backend without an n8n next to it stays silent
 * @property baseUrl Root of the organizer's n8n webhook endpoint
 * @property pathPrefix Path segment before the event name, shared by every workflow
 * @property eventId Conference identity stamped into every envelope
 * @property authToken Shared secret proving the event came from this backend; blank omits the header
 * @property timeoutMs Connect and read budget; a hanging n8n must not tie up a delivery thread
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
