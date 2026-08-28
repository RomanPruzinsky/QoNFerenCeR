package tr.qonferencer.screens.userCheck.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState

class EditUserDetailsViewModel(
	initialUser: UserDetailDto,
) : ViewModel() {
	private val _detail = MutableStateFlow(initialUser)
	val detail = _detail.asStateFlow()
	
	private val _updateState = initDataState<UserDetailDto>()
	val updateState = _updateState.asStateFlow()
	
	fun save(data: ModifyableUserDataDto) {
		dataStatedAction(_updateState) {
			val updated = QoNFerenCerApi.admin.updateUser(_detail.value.userId, data)
			_detail.value = updated
			updated
		}
	}
}

fun editUserDetailsViewModelFactory(user: UserDetailDto) = viewModelFactory {
	initializer { EditUserDetailsViewModel(user) }
}
