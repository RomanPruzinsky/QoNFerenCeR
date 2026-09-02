package tr.qonferencer.user

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.shared.dtos.UserDetailDto

/** Currently logged-in user's profile, loaded from splash */
class CurrentUser {
	private val _details = MutableStateFlow<UserDetailDto?>(null)
	val details = _details.asStateFlow()

	fun setDetails(me: UserDetailDto?) {
		_details.value = me
	}
}
