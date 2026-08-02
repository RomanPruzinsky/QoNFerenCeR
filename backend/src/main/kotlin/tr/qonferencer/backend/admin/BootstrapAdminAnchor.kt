package tr.qonferencer.backend.admin

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import tr.qonferencer.backend.user.UserAnchorService
import tr.qonferencer.backend.user.UserRepository

/** Anchors firsst admin created by keycloak */
@Component
class BootstrapAdminAnchor(
	private val kc: KeycloakAdminService,
	private val users: UserRepository,
	private val anchors: UserAnchorService,
	@param:Value($$"${qonferencer.keycloak.admin.bootstrap-username}") private val bootstrapUsername: String,
) : ApplicationRunner {
	override fun run(args: ApplicationArguments) {
		try {
			val (sub, fullName) = kc.findByUsername(bootstrapUsername) ?: run {
				log.warn("bootstrap admin '$bootstrapUsername' not found in Keycloak, no anchor created")
				return
			}
			
			if (users.findByKcSub(sub) == null) {
				anchors.ensure(sub, fullName)
				log.info("anchored bootstrap admin '$bootstrapUsername' ($sub)")
			}
		} catch (e: Exception) {
			log.warn("couldn't anchor bootstrap admin '$bootstrapUsername': ${e.message}")
		}
	}
	
	private companion object {
		val log: Logger = LoggerFactory.getLogger(BootstrapAdminAnchor::class.java)
	}
}
