package tr.qonferencer.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.shared.dtos.CustomScreenDto

/** Runtime-added screens loaded from splash */
class CustomScreens {
	private val _screens = MutableStateFlow<List<CustomScreenDto>>(emptyList())
	val screens = _screens.asStateFlow()
	
	fun setScreens(all: List<CustomScreenDto>) {
		_screens.value = all
	}
}
