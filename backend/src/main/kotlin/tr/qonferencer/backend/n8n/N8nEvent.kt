package tr.qonferencer.backend.n8n

import tr.qonferencer.shared.LiterallyAny
import java.time.Instant

/**
 * Something worth telling the organizer about, published inside the transaction that caused it
 * @property type What happened
 * @property data Type-specific payload, becomes [N8nEnvelope.data] verbatim
 */
data class N8nEvent(
	val type: EventType,
	val data: Map<String, LiterallyAny>,
)

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
	val eventType: EventType,
	val ts: Instant,
	val event: String,
	val data: Map<String, LiterallyAny>,
)
