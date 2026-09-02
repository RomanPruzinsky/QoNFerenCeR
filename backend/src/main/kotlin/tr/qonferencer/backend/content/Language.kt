package tr.qonferencer.backend.content

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Available language
 * @property code Language code ("en","sk",...)
 * @property name Readable name ("English","Slovenčina",...)
 * @property isDefault Whether is picked on first launch + fallback
 */
@Entity
@Table(name = "language")
class Language(

	@Id
	@Column(name = "code")
	var code: String,

	@Column(name = "name", nullable = false)
	var name: String,

	@Column(name = "is_default", nullable = false)
	var isDefault: Boolean,
)
