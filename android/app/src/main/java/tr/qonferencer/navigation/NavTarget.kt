package tr.qonferencer.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import tr.qonferencer.shared.dtos.CustomScreenDto

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
