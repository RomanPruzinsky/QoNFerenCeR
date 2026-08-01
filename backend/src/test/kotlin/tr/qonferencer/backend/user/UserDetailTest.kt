package tr.qonferencer.backend.user

import org.junit.jupiter.api.Test
import org.mockito.Mockito
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
import tr.qonferencer.backend.admin.KeycloakUserInfo
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.enums.Role
import java.util.UUID

/** The info-desk detail view reached after a name search: GET /api/v1/users/{userId} */
@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserDetailTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var users: UserRepository

	@MockitoBean
	private lateinit var keycloak: KeycloakAdminService

	private fun newUser(fullName: String): Long {
		val sub = UUID.randomUUID()
		users.insertIfAbsent(sub, ByteArray(32), fullName)
		val userId = users.findByKcSub(sub)!!.id
		Mockito.`when`(keycloak.info(sub)).thenReturn(KeycloakUserInfo("slot_007", Role.VOLUNTEER, true, false))
		return userId
	}

	@Test
	fun `organiser with the grant sees everything but the scan secret`() {
		val userId = newUser("Jana Kováčová")

		detail(userId, Role.ORGANISER).andExpect {
			status { isOk() }
			jsonPath("$.userId") { value(userId.toInt()) }
			jsonPath("$.fullName") { value("Jana Kováčová") }
			jsonPath("$.role") { value("VOLUNTEER") }
			jsonPath("$.isSpeaker") { value(true) }
			jsonPath("$.qrSecret") { doesNotExist() }
		}
	}

	@Test
	fun `volunteer is refused even with the grant`() {
		val userId = newUser("Peter Novák")

		detail(userId, Role.VOLUNTEER, canCheckByName = true).andExpect {
			status { isForbidden() }
		}
	}

	@Test
	fun `organiser without the grant is refused`() {
		val userId = newUser("Marek Kovacs")

		detail(userId, Role.ORGANISER, canCheckByName = false).andExpect {
			status { isForbidden() }
		}
	}

	@Test
	fun `unknown userId is 404`() {
		detail(404_404L, Role.ORGANISER).andExpect {
			status { isNotFound() }
		}
	}

	private fun detail(userId: Long, role: Role, canCheckByName: Boolean = true) =
		mockMvc.get(ApiPaths.USER_BY_ID.replace("{userId}", userId.toString())) {
			with(
				jwt().jwt {
					it.subject(UUID.randomUUID().toString())
						.claim("realm_access", mapOf("roles" to listOf(role.name)))
						.claim("canCheckByName", canCheckByName)
				},
			)
		}
}
