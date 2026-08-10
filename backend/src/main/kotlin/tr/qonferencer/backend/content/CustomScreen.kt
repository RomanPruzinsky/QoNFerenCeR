package tr.qonferencer.backend.content

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import tr.qonferencer.shared.dtos.CustomElement
import tr.qonferencer.shared.enums.Role

/**
 * Runtime-added screen
 * @property id Screen id
 * @property titleKey Key to match for title translations
 * @property minRole Minimum [Role] to see this screen
 * @property icon Key into client's icon options
 * @property body Displayed list of [CustomElement]
 */
@Entity
@Table(name = "custom_screen")
class CustomScreen(
	
	@Id
	@Column(name = "id")
	var id: String,
	
	@Column(name = "title_key", nullable = false)
	var titleKey: String,
	
	@Enumerated(EnumType.STRING)
	@Column(name = "min_role", nullable = false)
	var minRole: Role,
	
	@Column(name = "icon", nullable = false)
	var icon: String,
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "body", nullable = false, columnDefinition = "jsonb")
	var body: String,
)
