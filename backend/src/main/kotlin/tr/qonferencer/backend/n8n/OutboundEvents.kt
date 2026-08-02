package tr.qonferencer.backend.n8n

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/** How domain announces what it did, so call sites carry no knowledge of transport */
@Component
class OutboundEvents(
	private val publisher: ApplicationEventPublisher,
) {
	/** Announces [event]; delivery happens after current transaction commits */
	fun publish(event: OutboundEvent) = publisher.publishEvent(event)
}
