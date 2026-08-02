package tr.qonferencer.backend.admin

import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tr.qonferencer.shared.enums.Role
import java.util.UUID

/** Username, role and orthogonal flags read from Keycloak for a user who isn't the caller */
data class KeycloakUserInfo(
	val username: String,
	val role: Role,
	val isSpeaker: Boolean,
	val canCheckByName: Boolean,
)

/** Wrapper over Keycloak Admin API for slot user */
@Service
class KeycloakAdminService(
	keycloak: Keycloak,
	@Value($$"${qonferencer.keycloak.admin.realm}") realm: String,
) {
	private val realmRes = keycloak.realm(realm)
	private val usersRes = realmRes.users()

	/**
	 * Creates an enabled user with [role] and its orthogonal attribute flags
	 * @return User's Keycloak sub
	 */
	fun createUser(username: String, role: Role, isSpeaker: Boolean = false, canCheckByName: Boolean = false): UUID {
		val rep = UserRepresentation().apply {
			this.username = username
			isEnabled = true
			email = "$username@qonferencer.local"
			firstName = username
			lastName = "slot"
			attributes = keycloakedAttributes(isSpeaker, canCheckByName)
		}
		val sub = UUID.fromString(usersRes.create(rep).use { CreatedResponseUtil.getCreatedId(it) })
		userResource(sub).roles().realmLevel().add(listOf(realmRole(role)))
		return sub
	}

	/** Replaces [role] and the flags of an existing user; the realm role is swapped, not added */
	fun updateUser(sub: UUID, role: Role, isSpeaker: Boolean, canCheckByName: Boolean) {
		val userRes = userResource(sub)
		userRes.update(userRes.toRepresentation().apply { attributes = keycloakedAttributes(isSpeaker, canCheckByName) })
		val realmRoles = userRes.roles().realmLevel()
		val ours = realmRoles.listAll().filter { held -> Role.entries.any { it.name == held.name } }
		if (ours.isNotEmpty()) realmRoles.remove(ours)
		realmRoles.add(listOf(realmRole(role)))
	}

	/** Kills every active session, so a refresh token left on a lost phone stops working */
	fun logout(sub: UUID) {
		userResource(sub).logout()
	}

	/** Sets a fresh permanent password for the user */
	fun setPassword(sub: UUID, password: String) {
		val cred = CredentialRepresentation().apply {
			type = CredentialRepresentation.PASSWORD
			value = password
			isTemporary = false
		}
		userResource(sub).resetPassword(cred)
	}

	/** Current username of the Keycloak user */
	fun username(sub: UUID): String = userResource(sub).toRepresentation().username

	/** Username, role and orthogonal flags of an arbitrary user, for info-desk detail views */
	fun info(sub: UUID): KeycloakUserInfo {
		val userRes = userResource(sub)
		val rep = userRes.toRepresentation()
		val attrs = rep.attributes ?: emptyMap()
		return KeycloakUserInfo(
			username = rep.username,
			role = Role.highestAvailable(userRes.roles().realmLevel().listAll().map { it.name }),
			isSpeaker = attrs["isSpeaker"]?.firstOrNull()?.toBoolean() ?: false,
			canCheckByName = attrs["canCheckByName"]?.firstOrNull()?.toBoolean() ?: false,
		)
	}

	/** Deletes the Keycloak user */
	fun deleteUser(sub: UUID) {
		usersRes.delete(sub.toString()).close()
	}

	/**
	 * @return first admin created from `realm-export` 
	 * @throws IllegalStateException When admin is not found
	 */
	fun searchFirstAdmin(username: String): Pair<UUID, String> {
		val rep = usersRes.search(username, true).firstOrNull() ?: error("Username $username not found in Keycloak")
		return UUID.fromString(rep.id) to "${rep.firstName} ${rep.lastName}"
	}

// ///////////////// OPERATIONS ///////////////////
// ////////////////////////////////////////////////
// ////////////////// HELPERS /////////////////////

	/** Handle for [sub]'s user resource, used by every per-user API call */
	private fun userResource(sub: UUID) = usersRes.get(sub.toString())

	/** Realm's representation of [role] */
	private fun realmRole(role: Role) = realmRes.roles().get(role.name).toRepresentation()
	
	private fun keycloakedAttributes(isSpeaker: Boolean, canCheckByName: Boolean) = mapOf(
		"isSpeaker" to listOf(isSpeaker.toString()),
		"canCheckByName" to listOf(canCheckByName.toString()),
	)
}
