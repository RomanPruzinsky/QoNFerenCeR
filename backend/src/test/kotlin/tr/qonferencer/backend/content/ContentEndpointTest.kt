package tr.qonferencer.backend.content

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.TestcontainersConfiguration
import tr.qonferencer.backend.admin.KeycloakAdminService
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.enums.Role
import java.util.UUID

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContentEndpointTest {
	
	@Autowired
	private lateinit var mockMvc: MockMvc
	
	@Autowired
	private lateinit var users: UserRepository
	
	@MockitoBean
	private lateinit var keycloak: KeycloakAdminService
	
	@Test
	fun `splash returns seeded content, role-filtered, with an etag`() {
		// anonymous caller = ANONYM → sees only the ANONYM screen 'home', not the VISITOR 'agenda'
		mockMvc.get("/api/v1/splash").andExpect {
			status { isOk() }
			header { exists("ETag") }
			jsonPath("$.languages[0].code") { value("en") }
			jsonPath("$.customScreens.length()") { value(1) }
			jsonPath("$.customScreens[0].id") { value("home") }
			jsonPath("$.mealWindows[0].nameKey") { value("meal.lunch1.name") }
			jsonPath("$.me") { doesNotExist() }
		}
	}
	
	@Test
	fun `splash embeds the caller's own profile when authenticated, minus the scan secret`() {
		val sub = UUID.randomUUID()
		users.insertIfAbsent(sub, ByteArray(32), "Jana Kováčová")
		
		mockMvc.get("/api/v1/splash") {
			with(
				jwt().jwt {
					it.subject(sub.toString())
						.claim("realm_access", mapOf("roles" to listOf(Role.VOLUNTEER.name)))
						.claim("isSpeaker", true)
				},
			)
		}.andExpect {
			status { isOk() }
			jsonPath("$.me.fullName") { value("Jana Kováčová") }
			jsonPath("$.me.role") { value("VOLUNTEER") }
			jsonPath("$.me.isSpeaker") { value(true) }
			jsonPath("$.me.mealSecret") { doesNotExist() }
		}
	}
	
	@Test
	fun `custom screen above caller role is 403`() {
		// 'agenda' is minRole VISITOR; anonymous ANONYM must not reach it
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
