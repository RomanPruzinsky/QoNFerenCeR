package tr.qonferencer.backend.content

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import tr.qonferencer.shared.dtos.CustomElement
import tr.qonferencer.shared.dtos.TextSource
import tr.qonferencer.shared.enums.CustomTextSize
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomElementJsonTest {

	private val mapper = jacksonObjectMapper()

	// Serialize via the declared element type, exactly like Spring MVC does for a
	// `List<CustomElement>` return value — otherwise generics erase and the root
	// element loses its type discriminator.
	private val listWriter = mapper.writerFor(object : TypeReference<List<CustomElement>>() {})

	@Test
	fun `round-trips the sealed element tree`() {
		val tree: List<CustomElement> = listOf(
			CustomElement.Column(
				children = listOf(
					CustomElement.Row(
						children = listOf(
							CustomElement.Text(TextSource.Ref("label.company")),
							CustomElement.Text(TextSource.Link("https://n8n/agenda"), CustomTextSize.SMALL),
						),
					),
					CustomElement.Image("https://cdn/logo.png"),
				),
			),
		)
		val back = mapper.readValue<List<CustomElement>>(listWriter.writeValueAsString(tree))
		assertEquals(tree, back)
	}

	@Test
	fun `deserializes the seed body shape`() {
		val seed = """[{"type":"TEXT","source":{"kind":"REF","key":"home.welcome"},"size":"LARGE"}]"""
		val back = mapper.readValue<List<CustomElement>>(seed)
		assertEquals(
			listOf(CustomElement.Text(TextSource.Ref("home.welcome"), CustomTextSize.LARGE)),
			back,
		)
	}
}
