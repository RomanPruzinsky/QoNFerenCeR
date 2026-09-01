package tr.qonferencer.screens.admin.translations

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.shared.dtos.AllTranslationsDto
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState
import tr.qonferencer.trons.states.dataState.waiting

class TranslationsViewModel : ViewModel() {
	private val _allState = initDataState<AllTranslationsDto>()
	val allState = _allState.asStateFlow()

	private val _saveState = initDataState<AllTranslationsDto>()
	val saveState = _saveState.asStateFlow()

	init {
		refresh()
	}

	fun refresh() {
		dataStatedAction(_allState) { QoNFerenCerApi.admin.getTranslations() }
	}

	fun save(edited: AllTranslationsDto) {
		dataStatedAction(_saveState) {
			val updated = QoNFerenCerApi.admin.setTranslations(edited)
			refresh()
			updated
		}
	}

	fun resetSaveState() {
		_saveState.waiting()
	}
}
