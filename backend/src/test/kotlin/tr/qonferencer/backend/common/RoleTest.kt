package tr.qonferencer.backend.common

import tr.qonferencer.shared.enums.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoleTest {

	@Test
	fun `atLeast is a linear threshold`() {
		assertTrue(Role.ORGANISER.atLeast(Role.VISITOR))
		assertTrue(Role.VISITOR.atLeast(Role.VISITOR))
		assertFalse(Role.VISITOR.atLeast(Role.ORGANISER))
	}

	@Test
	fun `highest picks the strongest realm role`() {
		assertEquals(Role.ADMIN, Role.highest(listOf("VISITOR", "ADMIN", "ANONYM")))
		assertEquals(Role.ANONYM, Role.highest(emptyList()))
		assertEquals(Role.ANONYM, Role.highest(listOf("nonsense")))
	}

	@Test
	fun `fromOrAnonym is case-insensitive and safe`() {
		assertEquals(Role.VOLUNTEER, Role.fromOrAnonym("volunteer"))
		assertEquals(Role.ANONYM, Role.fromOrAnonym(null))
		assertEquals(Role.ANONYM, Role.fromOrAnonym("does-not-exist"))
	}
}
