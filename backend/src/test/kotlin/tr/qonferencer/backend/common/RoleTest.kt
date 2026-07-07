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
		assertEquals(Role.ADMIN, Role.highest(listOf("VISITOR", "ADMIN", "GUEST")))
		assertEquals(Role.GUEST, Role.highest(emptyList()))
		assertEquals(Role.GUEST, Role.highest(listOf("nonsense")))
	}

	@Test
	fun `fromOrGuest is case-insensitive and safe`() {
		assertEquals(Role.VOLUNTEER, Role.fromOrGuest("volunteer"))
		assertEquals(Role.GUEST, Role.fromOrGuest(null))
		assertEquals(Role.GUEST, Role.fromOrGuest("does-not-exist"))
	}
}
