package tr.qonferencer.trons.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember

/**
 * Composable state of [Float] with [init] initial value
 * @param init Initial value
 * @return [MutableState] of [Float] with [init] value
 */
@Composable
fun rememberFloat(init: Float) = remember { mutableFloatStateOf(init) }

/**
 * Composable state of [Float] with `0F` as initial value
 * @return [MutableState] of [Float] with `0F` value
 */
@Composable
fun remember0f() = rememberFloat(0F)
