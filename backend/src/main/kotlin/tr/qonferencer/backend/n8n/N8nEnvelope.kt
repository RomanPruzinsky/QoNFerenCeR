package tr.qonferencer.backend.n8n

import java.time.Instant

/**
 * What webhook receives
 * @property conferenceId Which conference this happened at
 * @property schemaVersion Contract version
 * @property timestamp When backend emitted it
 * @property eventType Which event this is
 * @property data Type-specific data
 */
data class N8nEnvelope(
	val conferenceId: String,
	val schemaVersion: Int,
	val timestamp: Instant,
	val eventType: String,
	val data: OutboundEvent,
)
