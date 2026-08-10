package tr.qonferencer.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import tr.qonferencer.shared.dtos.CustomScreenDto

private const val CUSTOM_ROUTE_PREFIX = "custom/"

/** One navigable target: either a compile-time [QoNFerenCeRDestinations] or a DB-defined [CustomScreenDto] */
sealed class NavTarget {
	abstract val route: String
	abstract val titleKey: String
	abstract val icon: ImageVector

	data class Fixed(
		val destination: QoNFerenCeRDestinations,
	) : NavTarget() {
		override val route = destination.name
		override val titleKey = destination.titleKey
		override val icon = destination.icon
	}

	data class Custom(
		val screen: CustomScreenDto,
	) : NavTarget() {
		override val route = "$CUSTOM_ROUTE_PREFIX${screen.id}"
		override val titleKey = screen.titleKey
		override val icon = iconFor(screen.icon)
	}

	companion object {
		/** Route registered for [Custom] targets */
		const val CUSTOM_ROUTE = "$CUSTOM_ROUTE_PREFIX{id}"
	}
}
