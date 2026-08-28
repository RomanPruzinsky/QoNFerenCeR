package tr.qonferencer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import tr.qonferencer.shared.dtos.CustomScreenDto
import tr.qonferencer.theme.colors

sealed class NavTarget {
	abstract val titleKey: String
	abstract val icon: ImageVector
	
	data class Fixed(
		val destination: QoNFerenCeRDestinations,
	) : NavTarget() {
		override val titleKey = destination.titleKey
		override val icon = destination.icon
	}
	
	data class Custom(
		val screen: CustomScreenDto,
	) : NavTarget() {
		override val titleKey = screen.titleKey
		override val icon = iconFor(screen.icon)
	}
}

/** Special color per nav target */
val NavTarget.customColor: Color?
	@Composable get() = when (this) {
		is NavTarget.Fixed -> when (destination) {
			QoNFerenCeRDestinations.LOGIN -> colors.action.approve
			QoNFerenCeRDestinations.USER_CHECK -> colors.level.organiser
			else -> null
		}
		
		is NavTarget.Custom -> null
	}
