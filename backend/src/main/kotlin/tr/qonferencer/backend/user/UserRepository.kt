package tr.qonferencer.backend.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserRepository : JpaRepository<User, Long> {

	fun findByKcSub(kcSub: UUID): User?

	/** Creates [User], uses `Keycloak` data */
	@Modifying
	@Query(
		value = """
			INSERT INTO app_user (kc_sub, qr_secret, qr_secret_v, consented, custom_json, created_at)
			VALUES (:kcSub, :qrSecret, 0, false, '{}'::jsonb, now())
			ON CONFLICT (kc_sub) DO NOTHING
		""",
		nativeQuery = true,
	)
	fun insertIfAbsent(@Param("kcSub") kcSub: UUID, @Param("qrSecret") qrSecret: ByteArray)
}
