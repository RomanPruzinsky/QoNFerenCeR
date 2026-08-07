package tr.qonferencer.trons.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow

////////////////////////////////////////
//////////////// INITS /////////////////

/**
 * Composable state of [Boolean] with `true` as initial value
 * @return [MutableState] of [Boolean] with `true` value
 */
@Composable
fun rememberTrue() = remember { mutableStateOf(true) }

/**
 * Composable state of [Boolean] with `false` false as initial value
 * @return [MutableState] of [Boolean] with `false` value
 */
@Composable
fun rememberFalse() = remember { mutableStateOf(false) }

/**
 * Composable state of [Boolean] with [cond] as initial value
 * @param cond Initial bool value
 * @return [MutableState] of [Boolean] with [cond] value
 */
@Composable
fun rememberBool(cond: Boolean) = remember { mutableStateOf(cond) }

/**
 * Composable state of [Boolean] with [cond] as initial value
 * @param cond Initial bool value
 * @return [MutableState] of [Boolean] with [cond] value
 */
@Composable
fun rememberBool(cond: () -> Boolean) = remember { mutableStateOf(cond()) }

/**
 * Composable state of [Boolean] with [cond] as initial value
 * @param cond Composable initial bool value
 * @return [MutableState] of [Boolean] with [cond] value
 */
@Composable
fun rememberBoolC(cond: @Composable () -> Boolean) = if (cond()) rememberTrue() else rememberFalse()

//////////////// INITS /////////////////
////////////////////////////////////////
//////////////// TOOLS /////////////////

/** Switches receiver of [MutableState] Boolean */
fun MutableState<Boolean>.switch() {
	this.value = !this.value
}

/** Switches receiver of [MutableStateFlow] Boolean */
fun MutableStateFlow<Boolean>.switch() {
	this.value = !this.value
}

//////////////// TOOLS /////////////////
////////////////////////////////////////
