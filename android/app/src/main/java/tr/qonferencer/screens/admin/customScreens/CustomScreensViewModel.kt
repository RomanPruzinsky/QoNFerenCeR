package tr.qonferencer.screens.admin.customScreens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.navigation.FALLBACK_ICON_KEY
import tr.qonferencer.shared.dtos.CustomScreenAdminDto
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.trons.miscs.EMPTY_STRING
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState
import tr.qonferencer.trons.states.dataState.waiting
import tr.qonferencer.trons.states.infoState.infoStatedAction
import tr.qonferencer.trons.states.infoState.initInfoState
import java.io.IOException

class CustomScreensViewModel : ViewModel() {
	private val _selected = MutableStateFlow<CustomScreenAdminDto?>(null)
	val selected = _selected.asStateFlow()

	private val _allScreensState = initDataState<List<CustomScreenAdminDto>>()
	val allScreensState = _allScreensState.asStateFlow()

	private val _saveState = initDataState<CustomScreenAdminDto>()
	val saveState = _saveState.asStateFlow()

	private val _deleteState = initInfoState()
	val deleteState = _deleteState.asStateFlow()

	init {
		refresh()
	}

	fun refresh() {
		dataStatedAction(_allScreensState) { QoNFerenCerApi.admin.listCustomScreens() }
	}

	fun select(screen: CustomScreenAdminDto) {
		_selected.value = screen
	}

	fun deselect() {
		_selected.value = null
	}

	fun create(id: String) {
		_selected.value = CustomScreenAdminDto(
			id = id,
			titleKey = EMPTY_STRING,
			icon = FALLBACK_ICON_KEY,
			minRole = Role.VISITOR,
			isStartingScreen = false,
			body = emptyList(),
		)
	}

	fun save(edited: CustomScreenAdminDto) {
		dataStatedAction(_saveState) {
			val updated = QoNFerenCerApi.admin.updateCustomScreen(edited.id, edited)
			_selected.value = updated
			refresh()
			updated
		}
	}

	fun resetSaveState() {
		_saveState.waiting()
	}

	fun delete(id: String) {
		infoStatedAction(_deleteState) {
			val response = QoNFerenCerApi.admin.deleteCustomScreen(id)
			if (!response.isSuccessful) throw IOException("Delete failed: ${response.code()}")
			_selected.value = null
			refresh()
		}
	}
}
