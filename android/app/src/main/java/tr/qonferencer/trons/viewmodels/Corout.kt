package tr.qonferencer.trons.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Thread callable inside [ViewModel]
 *
 * Running on [Dispatchers.IO]
 */
fun ViewModel.corout(action: suspend CoroutineScope.() -> Unit) {
	viewModelScope.launch(Dispatchers.IO) {
		action(this)
	}
}
