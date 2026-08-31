package tr.qonferencer.screens.userCheck.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.shared.dtos.LoginCredentialsDto
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState
import tr.qonferencer.trons.states.dataState.waiting
import tr.qonferencer.trons.states.infoState.infoStatedAction
import tr.qonferencer.trons.states.infoState.initInfoState
import java.io.IOException

class EditUserDetailsViewModel(
	initialUser: UserDetailDto,
) : ViewModel() {
	private val _detail = MutableStateFlow(initialUser)
	val detail = _detail.asStateFlow()

	private val _updateState = initDataState<UserDetailDto>()
	val updateState = _updateState.asStateFlow()

	private val _revokeState = initInfoState()
	val revokeState = _revokeState.asStateFlow()

	private val _deleteState = initInfoState()
	val deleteState = _deleteState.asStateFlow()
	
	private val _loginCredentialsState = initDataState<LoginCredentialsDto>()
	val loginCredentialsState = _loginCredentialsState.asStateFlow()

	fun save(data: ModifyableUserDataDto) {
		dataStatedAction(_updateState) {
			val updated = QoNFerenCerApi.admin.updateUser(_detail.value.userId, data)
			_detail.value = updated
			updated
		}
	}

	/** Rotates slot's mealSecret and kills its active sessions, for a lost/stolen device */
	fun revoke() {
		infoStatedAction(_revokeState) {
			val response = QoNFerenCerApi.admin.revoke(_detail.value.userId)
			if (!response.isSuccessful) throw IOException("Revoke failed: ${response.code()}")
		}
	}

	/** GDPR-erases slot; may leave its Keycloak account behind (207), still counts as success */
	fun delete() {
		infoStatedAction(_deleteState) {
			val response = QoNFerenCerApi.admin.deleteUser(_detail.value.userId)
			if (!response.isSuccessful) throw IOException("Delete failed: ${response.code()}")
		}
	}

	/** Generates and returns -new credentials for slot, invalidating its previous password */
	fun reissueCredentials() {
		dataStatedAction(_loginCredentialsState) { QoNFerenCerApi.admin.login(_detail.value.userId) }
	}
	
	fun resetLoginCredentials() {
		_loginCredentialsState.waiting()
	}

	fun reset() {
		_updateState.value = DataState.Waiting
	}
}

fun editUserDetailsViewModelFactory(user: UserDetailDto) = viewModelFactory {
	initializer { EditUserDetailsViewModel(user) }
}
