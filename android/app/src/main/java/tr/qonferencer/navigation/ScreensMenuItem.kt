package tr.qonferencer.navigation

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.theme.color
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.theme.defaultClipSize

@Composable
fun ScreensMenuItem(
	target: NavTarget,
	currentRole: Role,
	isSelected: Boolean,
	customColor: Color? = null,
	onClick: () -> Unit,
) {
	NavigationDrawerItem(
		label = {
			Text(
				text = dynamicTranslation(target.titleKey),
				style = typo.headlineMedium,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				color = customColor ?: typo.headlineMedium.color,
			)
		},
		selected = isSelected,
		onClick = onClick,
		icon = {
			Icon(
				imageVector = target.icon,
				contentDescription = "${target.icon}",
				tint = customColor ?: LocalContentColor.current,
			)
		},
		shape = CircleShape.copy(CornerSize(defaultClipSize)),
		colors = NavigationDrawerItemDefaults.colors(
			selectedContainerColor = currentRole.color,
			unselectedContainerColor = Color.Transparent,
		),
	)
}
