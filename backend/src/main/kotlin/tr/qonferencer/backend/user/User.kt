package tr.qonferencer.backend.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

/**
 * Conference attendee, holds what `Keycloak` can't
 * @property kcSub User's `Keycloak` identity
 * @property mealSecret HMAC secret for **QR/NFC** tokens
 * @property mealSecretV Version of used [mealSecret]
 * @property fullName Attendee's name
 * @property customData Custom data
 * @property createdAt When was user created
 */
@Entity
@Table(name = "app_user")
class User(

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long,

	@Column(name = "kc_sub", nullable = false, unique = true)
	var kcSub: UUID,

	@Column(name = "meal_secret", nullable = false)
	var mealSecret: ByteArray,

	@Column(name = "meal_secret_v", nullable = false)
	var mealSecretV: Short = 0,

	@Column(name = "full_name", nullable = false)
	var fullName: String,

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "custom_data", nullable = false, columnDefinition = "jsonb")
	var customData: String = "{}",

	@Column(name = "created_at", nullable = false)
	var createdAt: Instant = Instant.now(),
)
