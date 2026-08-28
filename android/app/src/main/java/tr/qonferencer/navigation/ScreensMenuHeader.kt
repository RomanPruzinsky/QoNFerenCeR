package tr.qonferencer.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.R
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.theme.color
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.remembers.remember0dp
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultLayoutPadding

@Composable
fun ScreensMenuHeader(onClick: () -> Unit) {
	val userDetails = QoNFerenCeRApp.currentUser.details.collectValue()
	val currentRole = UserDetailDto.roleOrAnonym(userDetails)
	var textBlockHeight by remember0dp()
	val localDensity = LocalDensity.current

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.defaultClip(Edge.BOTTOM, multiplier = 2F)
			.background(currentRole.color)
			.clickable(onClick = onClick)
			.windowInsetsPadding(WindowInsets.statusBars)
			.defaultLayoutPadding()
			.defaultLayoutPadding(Edge.VERTICAL),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(defaultLayoutPadding),
	) {
		Image(
			painter = painterResource(R.drawable.logo),
			contentDescription = null,
			contentScale = ContentScale.Inside,
			modifier = Modifier
				.size(textBlockHeight)
				.defaultClip()
				.background(colors.container),
		)

		Column(
			modifier = Modifier
				.onGloballyPositioned { textBlockHeight = with(localDensity) { it.size.height.toDp() } },
		) {
			Text(
				text = userDetails?.fullName ?: dynamicTranslation("app.name"),
				style = typo.headlineMedium,
				color = colors.text,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
			Text(
				text = "- " + currentRole.name.lowercase(),
				style = typo.bodyMedium,
				color = colors.text,
			)
		}
	}
}
