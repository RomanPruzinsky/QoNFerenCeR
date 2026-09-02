package tr.qonferencer.trons.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember

/**
 * Composable state of [Int] with [init] initial value
 * @param init Initial value
 * @return [MutableState] of [Int] with [init] value
 */
@Composable
fun rememberInt(init: Int) = remember { mutableIntStateOf(init) }

/**
 * Composable state of [Int] with `0` as initial value
 * @return [MutableState] of [Int] with `0` value
 */
@Composable
fun remember0() = rememberInt(0)

/**
 * Composable state of [Int] with `1` as initial value
 * @return [MutableState] of [Int] with `1` value
 */
@Composable
fun remember1() = rememberInt(1)

/** Increments receiver of [MutableState] Int */
fun MutableState<Int>.inc() {
	this.value++
}

/** Decrements receiver of [MutableState] Int */
fun MutableState<Int>.dec() {
	this.value--
}
