package tr.qonferencer.backend.n8n

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import java.time.Instant

/** Ships [N8nEvent]s to n8n and swallows the reply: no retry, no outbox, never fails a scan */
@Component
class N8nOutboundListener(
	private val n8nRestClient: RestClient,
	private val properties: N8nProperties,
) {
	/** Sends [event] after commit; `fallbackExecution` covers the callers without a transaction */
	@Async(N8nConfig.N8N_EXECUTOR)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	fun onEvent(event: N8nEvent) {
		if (!properties.enabled) return
		val envelope = N8nEnvelope(
			schemaVersion = SCHEMA_VERSION,
			eventType = event.type,
			ts = Instant.now(),
			event = properties.eventName,
			data = event.data,
		)
		try {
			n8nRestClient.post()
				.uri("/{prefix}/{eventType}", properties.pathPrefix, event.type.name)
				.contentType(MediaType.APPLICATION_JSON)
				.apply { if (properties.pathToken.isNotBlank()) header(TOKEN_HEADER, properties.pathToken) }
				.body(envelope)
				.retrieve()
				.toBodilessEntity()
		} catch (notFound: HttpClientErrorException.NotFound) {
			log.debug("no workflow listens on {}: {}", event.type, notFound.message)
		} catch (failure: Exception) {
			log.warn("n8n delivery of {} failed: {}", event.type, failure.message)
		}
	}

	private companion object {
		/** Bump only when the envelope stops being backwards compatible */
		const val SCHEMA_VERSION = 1

		/** Shared-secret header for n8n's Header Auth; no `X-` prefix per RFC 6648 */
		const val TOKEN_HEADER = "QN-Token"

		val log = LoggerFactory.getLogger(N8nOutboundListener::class.java)
	}
}
