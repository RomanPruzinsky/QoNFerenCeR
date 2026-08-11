package tr.qonferencer.navigation

import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tr.qonferencer.shared.dtos.CustomScreenDto
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.theme.colors
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn

@Composable
fun ScreensMenuLayout(
	currentTarget: NavTarget,
	currentRole: Role,
	customScreens: List<CustomScreenDto>,
	onSelect: (NavTarget) -> Unit,
	modifier: Modifier = Modifier,
) {
	val allTargets =
		QoNFerenCeRDestinations.entries
			.filter { currentRole.atLeast(it.minRole) }
			.filter { it != QoNFerenCeRDestinations.MY_PROFILE }
			.filter {
				if (it != QoNFerenCeRDestinations.LOGIN) true
				else currentRole == Role.ANONYM
			}
			.map(NavTarget::Fixed) +
			customScreens.map(NavTarget::Custom)

	ModalDrawerSheet(modifier = modifier, drawerContainerColor = colors.navigation) {
		ScreensMenuHeader { onSelect(NavTarget.Fixed(QoNFerenCeRDestinations.MY_PROFILE)) }

		ScrollableColumn {
			allTargets.forEach { target ->
				ScreensMenuItem(
					target = target,
					currentRole = currentRole,
					isSelected = target == currentTarget,
					onClick = { onSelect(target) },
				)
			}
		}
	}
}
