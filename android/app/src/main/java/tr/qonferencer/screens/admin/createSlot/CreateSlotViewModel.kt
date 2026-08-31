package tr.qonferencer.screens.admin.createSlot

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.SlotProvisionedDto
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState
import tr.qonferencer.trons.states.dataState.waiting

class CreateSlotViewModel : ViewModel() {
	private val _createState = initDataState<SlotProvisionedDto>()
	val createState = _createState.asStateFlow()

	fun create(data: ModifyableUserDataDto) {
		dataStatedAction(_createState) { QoNFerenCerApi.admin.addUser(data) }
	}

	/** Discards created slot's credentials */
	fun reset() {
		_createState.waiting()
	}
}
