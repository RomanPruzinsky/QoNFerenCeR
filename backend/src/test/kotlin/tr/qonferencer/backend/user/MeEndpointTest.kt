package tr.qonferencer.backend.user

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.TestcontainersConfiguration
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.enums.Role
import java.util.UUID

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MeEndpointTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var users: UserRepository

	@Test
	fun `provisioned identity gets its anchor`() {
		val sub = UUID.randomUUID()
		users.insertIfAbsent(sub, ByteArray(32) { 1 }, "Roman Pružinský")

		mockMvc.get(ApiPaths.Me.ROOT) { with(callerWith(sub, Role.VISITOR)) }.andExpect {
			status { isOk() }
			jsonPath("$.role") { value("VISITOR") }
			jsonPath("$.qrSecret") { exists() }
		}
	}

	@Test
	fun `identity that was never provisioned is 404`() {
		mockMvc.get(ApiPaths.Me.ROOT) { with(callerWith(UUID.randomUUID(), Role.VISITOR)) }.andExpect {
			status { isNotFound() }
		}
	}

	private fun callerWith(sub: UUID, role: Role) = jwt().jwt {
		it.subject(sub.toString())
			.claim("realm_access", mapOf("roles" to listOf(role.name)))
	}
}
