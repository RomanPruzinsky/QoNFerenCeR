package tr.qonferencer.screens.userCheck.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.screens.keyInputMethod.KeyInputMethod
import tr.qonferencer.screens.keyInputMethod.KeyInputMethodPicker
import tr.qonferencer.screens.login.NfcLoginScreen
import tr.qonferencer.screens.login.QrLoginScreen
import tr.qonferencer.screens.userCheck.UserCheckViewModel
import tr.qonferencer.screens.userCheck.searchByName.ManualUserCheckScreen
import tr.qonferencer.trons.states.dataState.DataStateLayout

@Composable
fun UserCheckScreen() {
	val userCheckVM = viewModel<UserCheckViewModel>()
	
	var selectedMethod by remember { mutableStateOf<KeyInputMethod?>(null) }
	
	BackHandler(enabled = selectedMethod != null) {
		selectedMethod = null
		userCheckVM.dismiss()
	}
	
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.SpaceEvenly,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		DataStateLayout(
			stateFlow = userCheckVM.detailState,
			bodyOnWaiting = {
				when (selectedMethod) {
					null -> KeyInputMethodPicker(introKey = "userCheck.by.intro", onSelect = { selectedMethod = it })
					KeyInputMethod.MANUAL -> ManualUserCheckScreen(onPick = userCheckVM::loadFromManual)
					KeyInputMethod.QR_BAR -> QrLoginScreen(onDecode = userCheckVM::loadFromScan)
					KeyInputMethod.NFC -> NfcLoginScreen(onDecode = userCheckVM::loadFromScan)
				}
			},
		) { user ->
			BackHandler {
				if (selectedMethod != KeyInputMethod.MANUAL) selectedMethod = null
				userCheckVM.dismiss()
			}
			UserCheckDetailScreen(user = user)
		}
	}
}
