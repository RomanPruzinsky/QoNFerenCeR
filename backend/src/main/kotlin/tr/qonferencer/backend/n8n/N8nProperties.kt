package tr.qonferencer.backend.n8n

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Where outbound events go
 * @property enabled Off by default, so a backend without an n8n next to it stays silent
 * @property baseUrl Root of the organizer's n8n webhook endpoint
 * @property pathPrefix Path segment before the event name, shared by every workflow
 * @property eventName Conference identity stamped into every envelope
 * @property pathToken Shared secret proving the event came from this backend; blank omits the header
 * @property timeoutMs Connect and read budget; a hanging n8n must not tie up a delivery thread
 */
@ConfigurationProperties(prefix = "qonferencer.n8n")
data class N8nProperties(
	val enabled: Boolean = false,
	val baseUrl: String = "http://localhost:5678/webhook",
	val pathPrefix: String = "qonferencer_base",
	val eventName: String = "local-dev",
	val pathToken: String = "",
	val timeoutMs: Long = 3000,
)
