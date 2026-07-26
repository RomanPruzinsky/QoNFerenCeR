package tr.qonferencer.backend.user

import org.junit.jupiter.api.BeforeEach
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
class SearchByNameTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var users: UserRepository

	@BeforeEach
	fun seedAttendees() {
		listOf("Roman Pružinský", "Jana Kováčová", "Peter Novák", "Marek Kovacs")
			.forEach { users.insertIfAbsent(UUID.randomUUID(), ByteArray(32), it) }
	}

	@Test
	fun `diacritics are folded away`() {
		search("pruz", Role.ORGANISER).andExpect {
			status { isOk() }
			jsonPath("$.length()") { value(1) }
			jsonPath("$[0].fullName") { value("Roman Pružinský") }
		}
	}

	@Test
	fun `a transposed pair of letters still finds the person`() {
		search("pruzinksy", Role.ORGANISER).andExpect {
			status { isOk() }
			jsonPath("$[0].fullName") { value("Roman Pružinský") }
		}
	}

	@Test
	fun `full name typed out narrows to one`() {
		search("roman pruzinsky", Role.ORGANISER).andExpect {
			status { isOk() }
			jsonPath("$.length()") { value(1) }
		}
	}

	@Test
	fun `a shared surname returns every candidate for the organizer to tell apart`() {
		search("kova", Role.ORGANISER).andExpect {
			status { isOk() }
			jsonPath("$.length()") { value(2) }
		}
	}

	@Test
	fun `unknown name is an empty list, not an error`() {
		search("zzzzz", Role.ORGANISER).andExpect {
			status { isOk() }
			jsonPath("$.length()") { value(0) }
		}
	}

	@Test
	fun `volunteer is refused even with the grant`() {
		search("pruz", Role.VOLUNTEER, canCheckByName = true).andExpect {
			status { isForbidden() }
		}
	}

	@Test
	fun `organiser without the grant is refused`() {
		search("pruz", Role.ORGANISER, canCheckByName = false).andExpect {
			status { isForbidden() }
		}
	}

	private fun search(query: String, role: Role, canCheckByName: Boolean = true) = mockMvc.get(ApiPaths.SEARCH_BY_NAME) {
		param("q", query)
		with(
			jwt().jwt {
				it.subject(UUID.randomUUID().toString())
					.claim("realm_access", mapOf("roles" to listOf(role.name)))
					.claim("canCheckByName", canCheckByName)
			},
		)
	}
}
