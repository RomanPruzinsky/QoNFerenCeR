package tr.qonferencer.backend.content

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import tr.qonferencer.shared.enums.Role

/**
 * Runtime-added screen
 * @property id Screen id
 * @property titleKey Translation key of the menu title
 * @property minRole Minimum role to see this screen
 * @property body Displayed content
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
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "body", nullable = false, columnDefinition = "jsonb")
	var body: String,
)
