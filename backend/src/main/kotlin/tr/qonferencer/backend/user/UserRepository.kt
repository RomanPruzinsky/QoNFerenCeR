package tr.qonferencer.backend.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
			INSERT INTO app_user (kc_sub, qr_secret, qr_secret_v, full_name, custom_data, created_at)
			VALUES (:kcSub, :qrSecret, 0, :fullName, '{}'::jsonb, now())
			ON CONFLICT (kc_sub) DO NOTHING
		""",
		nativeQuery = true,
	)
	fun insertIfAbsent(
		@Param("kcSub") kcSub: UUID,
		@Param("qrSecret") qrSecret: ByteArray,
		@Param("fullName") fullName: String,
	)

	/** Info-desk lookup: `LIKE` answers a partial name, `word_similarity` survives a typo */
	@Query(
		value = """
			SELECT * FROM app_user
			WHERE lower(immutable_unaccent(full_name)) LIKE '%' || lower(immutable_unaccent(:query)) || '%'
			   OR word_similarity(lower(immutable_unaccent(:query)), lower(immutable_unaccent(full_name)))
			      >= :threshold
			ORDER BY word_similarity(lower(immutable_unaccent(:query)), lower(immutable_unaccent(full_name)))
			         DESC, full_name
		""",
		countQuery = """
			SELECT count(*) FROM app_user
			WHERE lower(immutable_unaccent(full_name)) LIKE '%' || lower(immutable_unaccent(:query)) || '%'
			   OR word_similarity(lower(immutable_unaccent(:query)), lower(immutable_unaccent(full_name)))
			      >= :threshold
		""",
		nativeQuery = true,
	)
	fun searchByName(@Param("query") query: String, @Param("threshold") threshold: Double, pageable: Pageable): Page<User>
}
