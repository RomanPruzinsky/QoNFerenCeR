package tr.qonferencer.backend.admin

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
class AdminEndpointTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Test
	fun `admin reaches the slot list`() {
		mockMvc.get(ApiPaths.Admin.SLOTS) { with(callerWith(Role.ADMIN)) }.andExpect {
			status { isOk() }
		}
	}

	@Test
	fun `organiser stays below the admin threshold`() {
		mockMvc.get(ApiPaths.Admin.SLOTS) { with(callerWith(Role.ORGANISER)) }.andExpect {
			status { isForbidden() }
		}
	}

	@Test
	fun `anonymous caller is unauthorized`() {
		mockMvc.get(ApiPaths.Admin.SLOTS).andExpect {
			status { isUnauthorized() }
		}
	}

	private fun callerWith(role: Role) = jwt().jwt {
		it.subject(UUID.randomUUID().toString())
			.claim("realm_access", mapOf("roles" to listOf(role.name)))
	}
}
