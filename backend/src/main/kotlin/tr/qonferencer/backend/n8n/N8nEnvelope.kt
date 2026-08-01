package tr.qonferencer.backend.n8n

import java.time.Instant

/**
 * What the webhook actually receives; the shape is fixed, so a new field breaks nothing
 * @property schemaVersion Envelope contract version
 * @property eventType Which event this is, mirroring the path segment
 * @property ts When the backend emitted it
 * @property event Which conference this instance serves
 * @property data Type-specific payload
 */
data class N8nEnvelope(
	val schemaVersion: Int,
	val eventType: String,
	val ts: Instant,
	val event: String,
	val data: OutboundEvent,
)
