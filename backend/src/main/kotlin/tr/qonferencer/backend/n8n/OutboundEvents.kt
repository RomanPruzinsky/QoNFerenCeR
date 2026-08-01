package tr.qonferencer.backend.n8n

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/** How the domain announces what it did, so call sites carry no knowledge of the transport */
@Component
class OutboundEvents(
	private val publisher: ApplicationEventPublisher,
) {
	/** Announces [event]; delivery happens after the current transaction commits */
	fun publish(event: OutboundEvent) = publisher.publishEvent(event)
}
