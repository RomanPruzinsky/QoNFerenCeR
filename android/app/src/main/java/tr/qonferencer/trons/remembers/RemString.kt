package tr.qonferencer.trons.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import tr.qonferencer.trons.miscs.EMPTY_STRING

/**
 * Composable state of [String] with [init] initial value
 * @param init Initial value
 * @return [MutableState] of [String] with [init] value
 */
@Composable
fun rememberString(init: String) = remember { mutableStateOf(init) }

/**
 * Composable state of [String] with [EMPTY_STRING] as initial value
 * @return [MutableState] of [String] with [EMPTY_STRING] value
 */
@Composable
fun rememberEmptyString() = remember { mutableStateOf(EMPTY_STRING) }

/** Clears receiver of [MutableState] String */
fun MutableState<String>.clear() {
	this@clear.value = EMPTY_STRING
}

/** Clears receiver of [MutableStateFlow] String */
fun MutableStateFlow<String>.clear() {
	this@clear.value = EMPTY_STRING
}
