package tr.qonferencer.backend.content

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import tr.qonferencer.backend.TestcontainersConfiguration

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
class ContentEndpointTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Test
	fun `splash returns seeded content, role-filtered, with an etag`() {
		// anonymous caller = GUEST → sees only the GUEST screen 'home', not the VISITOR 'agenda'
		mockMvc.get("/api/v1/splash").andExpect {
			status { isOk() }
			header { exists("ETag") }
			jsonPath("$.languages[0].code") { value("en") }
			jsonPath("$.customScreens.length()") { value(1) }
			jsonPath("$.customScreens[0].id") { value("home") }
			jsonPath("$.mealWindows[0].nameKey") { value("meal.lunch1.name") }
		}
	}

	@Test
	fun `custom screen above caller role is 403`() {
		// 'agenda' is minRole VISITOR; anonymous GUEST must not reach it
		mockMvc.get("/api/v1/custom-screens/agenda").andExpect {
			status { isForbidden() }
		}
	}

	@Test
	fun `custom screen body renders the sealed element`() {
		mockMvc.get("/api/v1/custom-screens/home").andExpect {
			status { isOk() }
			jsonPath("$[0].type") { value("TEXT") }
			jsonPath("$[0].source.kind") { value("REF") }
			jsonPath("$[0].source.key") { value("home.welcome") }
			jsonPath("$[0].size") { value("LARGE") }
		}
	}

	@Test
	fun `unknown custom screen is 404`() {
		mockMvc.get("/api/v1/custom-screens/does-not-exist").andExpect {
			status { isNotFound() }
		}
	}
}
