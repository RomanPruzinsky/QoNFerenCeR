package tr.qonferencer.backend.n8n

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.client.RestClient
import java.time.Duration

/** Setting that error on n8n don't break BE */
@Configuration
@EnableAsync
@EnableConfigurationProperties(N8nProperties::class)
@ConditionalOnProperty(name = [N8nProperties.ENABLED_PROPERTY], havingValue = N8nProperties.ENABLED_VALUE)
class N8nConfig {
	
	/** Sets up client that talks to n8n */
	@Bean
	fun n8nRestClient(
		builder: RestClient.Builder,
		properties: N8nProperties,
	): RestClient {
		val factory = SimpleClientHttpRequestFactory().apply {
			setConnectTimeout(Duration.ofMillis(properties.timeoutMs))
			setReadTimeout(Duration.ofMillis(properties.timeoutMs))
		}
		return builder
			.baseUrl(properties.baseUrl)
			.requestFactory(factory)
			.build()
	}

	/** Sets margins */
	@Bean(N8N_EXECUTOR)
	fun n8nMarginer(): ThreadPoolTaskExecutor = ThreadPoolTaskExecutor().apply {
		corePoolSize = 1
		maxPoolSize = 4
		queueCapacity = 256
		
		setThreadNamePrefix("n8n-")
		
		setRejectedExecutionHandler { _, _ -> log.warn("n8n delivery queue full, event dropped") }
		
		setWaitForTasksToCompleteOnShutdown(true)
		setAwaitTerminationSeconds(5)
	}
	
	companion object {
		const val N8N_EXECUTOR = "n8nExecutor"
		private val log = LoggerFactory.getLogger(N8nConfig::class.java)
	}
}
