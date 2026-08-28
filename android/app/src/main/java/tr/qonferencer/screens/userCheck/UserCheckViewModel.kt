package tr.qonferencer.screens.userCheck

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.scan.ScanToken
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState
import tr.qonferencer.trons.states.dataState.waiting

class UserCheckViewModel : ViewModel() {
	private val _detailState = initDataState<UserDetailDto>()
	val detailState = _detailState.asStateFlow()
	
	fun loadFromScan(payload: String) {
		dataStatedAction(_detailState) {
			val userId = ScanToken.parse(payload)?.userId ?: throw IllegalArgumentException("Unreadable user token")
			QoNFerenCerApi.user.byId(userId)
		}
	}
	
	fun loadFromManual(userId: Long) {
		dataStatedAction(_detailState) { QoNFerenCerApi.user.byId(userId) }
	}

	/** Discards resolved user */
	fun dismiss() {
		_detailState.waiting()
	}
}
