package tr.qonferencer.navigation

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.theme.defaultClipSize

@Composable
fun ScreensMenuItem(target: NavTarget, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
	NavigationDrawerItem(
		label = {
			Text(
				text = dynamicTranslation(target.titleKey),
				style = typo.headlineMedium,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		},
		selected = isSelected,
		onClick = onClick,
		icon = { Icon(imageVector = target.icon, contentDescription = "${target.icon}") },
		shape = CircleShape.copy(CornerSize(defaultClipSize)),
		colors = NavigationDrawerItemDefaults.colors(
			selectedContainerColor = colors.selected,
			unselectedContainerColor = Color.Transparent,
		),
		modifier = modifier,
	)
}
