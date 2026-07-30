package tr.qonferencer.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.ShallowEtagHeaderFilter

/** HTTP ETag / 304 handling for responses (splash caching) */
@Configuration
class WebConfig {
	@Bean
	fun shallowEtagHeaderFilter() = ShallowEtagHeaderFilter()
}
