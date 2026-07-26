package tr.qonferencer.backend.n8n

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.client.RestClient
import java.time.Duration

/** Wiring for outbound delivery: its own HTTP client and its own threads */
@Configuration
@EnableAsync
@EnableConfigurationProperties(N8nProperties::class)
class N8nConfig {

	/** The client that talks to n8n, built from Boot's builder so it shares the `ObjectMapper` */
	@Bean
	fun n8nRestClient(builder: RestClient.Builder, properties: N8nProperties): RestClient {
		val factory = SimpleClientHttpRequestFactory().apply {
			setConnectTimeout(Duration.ofMillis(properties.timeoutMs))
			setReadTimeout(Duration.ofMillis(properties.timeoutMs))
		}
		return builder
			.baseUrl(properties.baseUrl)
			.requestFactory(factory)
			.build()
	}

	/** Delivery threads, bounded; a full queue drops the event rather than growing a backlog */
	@Bean(N8N_EXECUTOR)
	fun n8nExecutor(): ThreadPoolTaskExecutor = ThreadPoolTaskExecutor().apply {
		corePoolSize = 1
		maxPoolSize = 4
		queueCapacity = QUEUE_CAPACITY
		setThreadNamePrefix("n8n-")
		setRejectedExecutionHandler { _, _ -> log.warn("n8n delivery queue full, event dropped") }
		setWaitForTasksToCompleteOnShutdown(true)
		setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS)
	}

	companion object {
		const val N8N_EXECUTOR = "n8nExecutor"

		private const val QUEUE_CAPACITY = 256
		private const val AWAIT_TERMINATION_SECONDS = 5

		private val log = LoggerFactory.getLogger(N8nConfig::class.java)
	}
}
