package tr.qonferencer.screens.customScreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import tr.qonferencer.shared.dtos.CustomElement
import tr.qonferencer.shared.dtos.TextSource
import tr.qonferencer.shared.enums.CustomTextSize
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.defaultHorizontalSpacing
import tr.qonferencer.trons.defaultLayouts.defaultVerticalSpacing
import tr.qonferencer.trons.miscs.EMPTY_STRING
import tr.qonferencer.trons.miscs.UNKNOWN_TEXT
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

/** Draws [this] recursively */
@Composable
fun CustomElement.Render() {
	when (this) {
		is CustomElement.Text ->
			Text(
				text = resolveText(source),
				style = size.toTypo(),
				modifier = Modifier.defaultTextPadding(),
			)

		is CustomElement.Image -> AsyncImage(
			model = url,
			contentDescription = null,
			contentScale = ContentScale.FillWidth,
			placeholder = rememberVectorPainter(Icons.Default.Image),
			error = rememberVectorPainter(Icons.Default.BrokenImage),
			modifier = Modifier
				.defaultLayoutPadding()
				.fillMaxWidth()
				.scale(1F),
		)

		is CustomElement.Row ->
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.horizontalScroll(rememberScrollState()),
				horizontalArrangement = defaultHorizontalSpacing(),
				verticalAlignment = Alignment.CenterVertically,
				content = { children.forEach { it.Render() } },
			)

		is CustomElement.Column ->
			Column(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = defaultVerticalSpacing(),
				horizontalAlignment = Alignment.CenterHorizontally,
				content = { children.forEach { it.Render() } },
			)
	}
}

@Composable
private fun resolveText(source: TextSource): String = when (source) {
	is TextSource.Ref -> dynamicTranslation(source.key)
	is TextSource.Link -> {
		var text by remember(source.url) { mutableStateOf(EMPTY_STRING) }
		LaunchedEffect(source.url) { text = runCatching { fetchLinkText(source.url) }.getOrDefault(UNKNOWN_TEXT) }
		text
	}
}

@Composable
private fun CustomTextSize.toTypo() = when (this) {
	CustomTextSize.SMALL -> typo.bodySmall
	CustomTextSize.MEDIUM -> typo.bodyLarge
	CustomTextSize.LARGE -> typo.headlineMedium
}

private val linkTextClient by lazy { OkHttpClient() }

/** @return Raw text from [url] */
private suspend fun fetchLinkText(url: String): String = withContext(Dispatchers.IO) {
	linkTextClient.newCall(Request.Builder().url(url).build()).execute().use { it.body.string() }
}
