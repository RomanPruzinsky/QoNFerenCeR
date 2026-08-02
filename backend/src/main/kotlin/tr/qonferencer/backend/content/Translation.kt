package tr.qonferencer.backend.content

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.io.Serializable

/** Composite key for [Translation] */
@Embeddable
data class TranslationId(
	@Column(name = "key")
	var key: String,
	@Column(name = "lang_code")
	var langCode: String,
) : Serializable

/**
 * Translation entry for key per language
 * @property id Composite key (translation key + language code)
 * @property text Text to display for [id]
 */
@Entity
@Table(name = "translation")
class Translation(
	
	@EmbeddedId
	var id: TranslationId,
	
	@Column(name = "text", nullable = false, columnDefinition = "text")
	var text: String,
)
