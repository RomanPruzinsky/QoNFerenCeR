package tr.qonferencer.screens.admin.translations

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.shared.dtos.AllTranslationsDto
import tr.qonferencer.shared.dtos.TranslationDto
import tr.qonferencer.trons.miscs.EMPTY_STRING
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState
import tr.qonferencer.trons.states.dataState.waiting

class TranslationsViewModel : ViewModel() {
	private val _allState = initDataState<AllTranslationsDto>()
	val allState = _allState.asStateFlow()

	private val _saveState = initDataState<AllTranslationsDto>()
	val saveState = _saveState.asStateFlow()

	/** What is filtered by */
	val keySearch: MutableState<String> = mutableStateOf(EMPTY_STRING)

	/** Distinct keys of [translations] fuzzily caselessly matching [keySearch] */
	fun filterKeys(translations: List<TranslationDto>): List<String> {
		val pattern =
			Regex(
				pattern = keySearch.value.map { character -> Regex.escape(character.toString()) }.joinToString(".*"),
				option = RegexOption.IGNORE_CASE,
			)
		return translations
			.relist { it.key }
			.distinct()
			.filter { key -> pattern.containsMatchIn(key) }
	}

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
