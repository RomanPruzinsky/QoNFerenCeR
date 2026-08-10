package tr.qonferencer.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.theme.color
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultClipSize
import tr.qonferencer.trons.theme.defaultLayoutPadding

@Composable
fun ScreensMenuHeader(modifier: Modifier = Modifier) {
	val userDetails = QoNFerenCeRApp.currentUser.details.collectValue()
	val currentRole = UserDetailDto.roleOrAnonym(userDetails)
	
	Row(
		modifier = modifier
			.fillMaxWidth()
			.defaultClip(Edge.BOTTOM, multiplier = 2F)
			.background(currentRole.color)
			.windowInsetsPadding(WindowInsets.statusBars)
			.defaultLayoutPadding(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(defaultLayoutPadding),
	) {
		Box(
			modifier = Modifier
				.size(defaultClipSize * 3)
				.defaultClip()
				.background(colors.container),
		)
		
		Column {
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
