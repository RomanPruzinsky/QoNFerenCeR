package tr.qonferencer.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.shared.dtos.MealWindowDto

/** Meal serving windows loaded from splash */
class MealWindows {
	private val _windows = MutableStateFlow<List<MealWindowDto>>(emptyList())
	val windows = _windows.asStateFlow()

	fun setWindows(all: List<MealWindowDto>) {
		_windows.value = all
	}
}
