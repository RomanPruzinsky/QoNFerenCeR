package tr.qonferencer.backend.n8n

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import tr.qonferencer.shared.LiteralyAny

/** How the domain announces what it did, so call sites carry no knowledge of the transport */
@Component
class OutboundEvents(
	private val publisher: ApplicationEventPublisher,
) {
	/** Announces [type] with its [data]; delivery happens after the current transaction commits */
	fun publish(type: EventType, data: Map<String, LiteralyAny>) = publisher.publishEvent(N8nEvent(type, data))
}
