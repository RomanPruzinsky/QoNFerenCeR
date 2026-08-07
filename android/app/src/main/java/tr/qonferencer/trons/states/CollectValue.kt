package tr.qonferencer.trons.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow

/** Collects current value of [StateFlow] */
@Composable
fun <T> StateFlow<T>.collectValue(): T = this.collectAsState().value
