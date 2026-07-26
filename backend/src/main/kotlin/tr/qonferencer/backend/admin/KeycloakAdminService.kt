package tr.qonferencer.backend.admin

import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tr.qonferencer.shared.enums.Role
import java.util.UUID

/** Wrapper over Keycloak Admin API for slot user */
@Service
class KeycloakAdminService(
	private val keycloak: Keycloak,
	@param:Value($$"${qonferencer.keycloak.admin.realm}") private val realm: String,
) {
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
			attributes = mapOf(
				"isSpeaker" to listOf(isSpeaker.toString()),
				"canCheckByName" to listOf(canCheckByName.toString()),
			)
		}
		val realmRes = keycloak.realm(realm)
		val sub = realmRes.users().create(rep).use { CreatedResponseUtil.getCreatedId(it) }
		val roleRep = realmRes.roles().get(role.name).toRepresentation()
		realmRes.users().get(sub).roles().realmLevel().add(listOf(roleRep))
		return UUID.fromString(sub)
	}

	/** Replaces [role] and the flags of an existing user; the realm role is swapped, not added */
	fun updateUser(sub: UUID, role: Role, isSpeaker: Boolean, canCheckByName: Boolean) {
		val realmRes = keycloak.realm(realm)
		val userRes = realmRes.users().get(sub.toString())
		userRes.update(
			userRes.toRepresentation().apply {
				attributes = mapOf(
					"isSpeaker" to listOf(isSpeaker.toString()),
					"canCheckByName" to listOf(canCheckByName.toString()),
				)
			},
		)
		val realmRoles = userRes.roles().realmLevel()
		val ours = realmRoles.listAll().filter { held -> Role.entries.any { it.name == held.name } }
		if (ours.isNotEmpty()) realmRoles.remove(ours)
		realmRoles.add(listOf(realmRes.roles().get(role.name).toRepresentation()))
	}

	/** Kills every active session, so a refresh token left on a lost phone stops working */
	fun logout(sub: UUID) {
		keycloak.realm(realm).users().get(sub.toString()).logout()
	}

	/** Sets a fresh permanent password for the user */
	fun setPassword(sub: UUID, password: String) {
		val cred = CredentialRepresentation().apply {
			type = CredentialRepresentation.PASSWORD
			value = password
			isTemporary = false
		}
		keycloak.realm(realm).users().get(sub.toString()).resetPassword(cred)
	}

	/** Current username of the Keycloak user */
	fun username(sub: UUID): String = keycloak.realm(realm).users().get(sub.toString()).toRepresentation().username

	/** Deletes the Keycloak user */
	fun deleteUser(sub: UUID) {
		keycloak.realm(realm).users().delete(sub.toString()).close()
	}
}
