package tr.qonferencer.backend.n8n

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import java.time.Instant

/** Ships [OutboundEvent] to n8n and ignores reply */
@Component
@ConditionalOnProperty(name = [N8nProperties.ENABLED_PROPERTY], havingValue = N8nProperties.ENABLED_VALUE)
class N8nOutboundListener(
	private val n8nRestClient: RestClient,
	private val properties: N8nProperties,
) {
	/** Sends [event] */
	@Async(N8nConfig.N8N_EXECUTOR)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	fun sendMessageToN8n(event: OutboundEvent) {
		if (!properties.enabled) return

		val envelope = N8nEnvelope(
			conferenceId = properties.eventId,
			schemaVersion = SCHEMA_VERSION,
			timestamp = Instant.now(),
			eventType = event.type,
			data = event,
		)

		try {
			n8nRestClient.post()
				.uri("/${properties.pathPrefix}/${event.type}")
				.contentType(MediaType.APPLICATION_JSON)
				.header(TOKEN_HEADER, properties.authToken)
				.body(envelope)
				.retrieve()
				.toBodilessEntity()
		} catch (notFound: HttpClientErrorException.NotFound) {
			log.debug("no workflow listens on ${event.type}: ${notFound.message}")
		} catch (e: Exception) {
			log.warn("n8n delivery of ${event.type} failed: ${e.message}")
		}
	}

	internal companion object {
		/** Upgrade only if [OutboundEvent] stops being backward compatible */
		const val SCHEMA_VERSION = 1

		/** Shared secret header for n8n's Header Auth */
		const val TOKEN_HEADER = "QN-Token"

		private val log: Logger = LoggerFactory.getLogger(N8nOutboundListener::class.java)
	}
}
