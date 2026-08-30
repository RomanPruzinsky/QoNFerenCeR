package tr.qonferencer.backend.admin

import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tr.qonferencer.shared.enums.Role
import java.util.UUID

/** User's data read from Keycloak */
data class KeycloakUserInfo(
	val username: String,
	val role: Role,
	val isSpeaker: Boolean,
	val canCheckUsers: Boolean,
)

/** Wrapper over Keycloak Admin API for slot user */
@Service
class KeycloakAdminService(
	keycloak: Keycloak,
	@Value($$"${qonferencer.keycloak.realm}") realm: String,
) {
	private val realmRes = keycloak.realm(realm)
	private val usersRes = realmRes.users()

	/**
	 * Creates enabled user with [role] and its orthogonal attribute flags
	 * @return User's Keycloak sub
	 */
	fun createUser(
		username: String,
		role: Role,
		isSpeaker: Boolean = false,
		canCheckUsers: Boolean = false,
	): UUID {
		val rep = UserRepresentation().apply {
			this.username = username
			isEnabled = true
			email = "$username@qonferencer.local"
			firstName = username
			lastName = "slot"
			attributes = keycloakedAttributes(isSpeaker, canCheckUsers)
		}
		val sub = UUID.fromString(usersRes.create(rep).use { CreatedResponseUtil.getCreatedId(it) })
		userResource(sub).roles().realmLevel().add(listOf(realmRole(role)))
		return sub
	}

	/** Replaces [role] and flags of existing user; realm role is swapped, not added */
	fun updateUser(
		sub: UUID,
		role: Role,
		isSpeaker: Boolean,
		canCheckUsers: Boolean,
	) {
		val userRes = userResource(sub)
		userRes.update(userRes.toRepresentation().apply { attributes = keycloakedAttributes(isSpeaker, canCheckUsers) })
		
		val realmRoles = userRes.roles().realmLevel()
		
		val ours = realmRoles.listAll().filter { held -> Role.entries.any { it.name == held.name } }
		if (ours.isNotEmpty()) realmRoles.remove(ours)
		
		realmRoles.add(listOf(realmRole(role)))
	}

	/** Kills every active session, so refresh token left on lost phone stops working */
	fun logout(sub: UUID) {
		userResource(sub).logout()
	}

	/** Sets fresh permanent password for user */
	fun setPassword(
		sub: UUID,
		password: String,
	) {
		val cred = CredentialRepresentation().apply {
			type = CredentialRepresentation.PASSWORD
			value = password
			isTemporary = false
		}
		userResource(sub).resetPassword(cred)
	}

	/** Current username of Keycloak user */
	fun username(sub: UUID): String = userResource(sub).toRepresentation().username

	/** @return Keycloak data for user */
	fun info(sub: UUID): KeycloakUserInfo {
		val userRes = userResource(sub)
		val rep = userRes.toRepresentation()
		val attrs = rep.attributes ?: emptyMap()
		
		return KeycloakUserInfo(
			username = rep.username,
			role = Role.highestAvailable(userRes.roles().realmLevel().listAll().map { it.name }),
			isSpeaker = attrs["isSpeaker"]?.firstOrNull()?.toBoolean() ?: false,
			canCheckUsers = attrs["canCheckUsers"]?.firstOrNull()?.toBoolean() ?: false,
		)
	}

	/** Deletes Keycloak user */
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
	
	private fun keycloakedAttributes(
		isSpeaker: Boolean,
		canCheckUsers: Boolean,
	) = mapOf(
		"isSpeaker" to listOf(isSpeaker.toString()),
		"canCheckUsers" to listOf(canCheckUsers.toString()),
	)
}
