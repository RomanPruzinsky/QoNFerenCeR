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
	canCheckByName: Boolean,
	customScreens: List<CustomScreenDto>,
	onSelect: (NavTarget) -> Unit,
	modifier: Modifier = Modifier,
) {
	val allTargets =
		QoNFerenCeRDestinations.entries
			.asSequence()
			.filter { currentRole.atLeast(it.minRole) }
			.filter { it != QoNFerenCeRDestinations.MY_PROFILE }
			.filter {
				if (it != QoNFerenCeRDestinations.LOGIN) true
				else currentRole == Role.ANONYM
			}
			.filter {
				if (it != QoNFerenCeRDestinations.USER_CHECK) true
				else currentRole == Role.ADMIN || canCheckByName
			}
			.map(NavTarget::Fixed)
			.toList() +
			customScreens.map(NavTarget::Custom)

	ModalDrawerSheet(modifier = modifier, drawerContainerColor = colors.navigation) {
		ScreensMenuHeader {
			if (currentRole.atLeast(QoNFerenCeRDestinations.MY_PROFILE.minRole)) {
				onSelect(NavTarget.Fixed(QoNFerenCeRDestinations.MY_PROFILE))
			}
		}

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
