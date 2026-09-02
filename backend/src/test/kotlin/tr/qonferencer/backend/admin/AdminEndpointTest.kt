package tr.qonferencer.backend.admin

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tr.qonferencer.backend.TestcontainersConfiguration
import tr.qonferencer.backend.user.UserRepository
import tr.qonferencer.shared.ApiPaths
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserMealEntryDto
import tr.qonferencer.shared.enums.Role
import java.util.UUID
import kotlin.test.assertEquals

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminEndpointTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Autowired
	private lateinit var users: UserRepository

	/** A bad meal window must cost nothing: the Keycloak user is created outside the transaction */
	@Test
	fun `an unknown meal window is refused before any account exists`() {
		val before = users.count()

		mockMvc.post(ApiPaths.Admin.ADD_USER) {
			with(callerWith(Role.ADMIN))
			contentType = MediaType.APPLICATION_JSON
			content = objectMapper.writeValueAsString(
				ModifyableUserDataDto("Jana Nováková", meals = listOf(UserMealEntryDto(404_404L, "meal.vegan"))),
			)
		}.andExpect { status { isBadRequest() } }

		assertEquals(before, users.count())
	}

	private fun callerWith(role: Role) = jwt().jwt {
		it.subject(UUID.randomUUID().toString())
			.claim("realm_access", mapOf("roles" to listOf(role.name)))
	}
}
