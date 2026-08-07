package tr.qonferencer.trons.ops

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Returns [this] if [cond] is `true`, `null` otherwise
 * @receiver [T] to get if [cond]
 * @param cond Condition to eval
 * @return [this] if [cond] is `true`, `null` otherwise
 */
fun <T> T.nullOrThisIf(cond: Boolean): T? = if (cond) this else null

/**
 * Returns [this] if [cond] is `true`, `null` otherwise
 * @receiver [T] to get if [cond]
 * @param cond Condition to eval, arguement is [T] receiver
 * @return [this] if [cond] is `true`, `null` otherwise
 */
fun <T> T.nullOrThisIf(cond: (T) -> Boolean): T? = this.nullOrThisIf(cond(this))

/**
 * Returns `null` if [cond] is `true`, [this] otherwise
 * @receiver [T] to get if ![cond]
 * @param cond Condition to eval
 * @return `null` if [cond] is `true`, [this] otherwise
 */
fun <T> T.orNullIf(cond: Boolean): T? = this.nullOrThisIf(!cond)

/**
 * Returns `null` if [cond] is `true`, [this] otherwise
 * @receiver [T] to get if ![cond]
 * @param cond Condition to eval, arguement is [this] receiver
 * @return `null` if [cond] is `true`, [this] otherwise
 */
fun <T> T.orNullIf(cond: (T) -> Boolean): T? = this.orNullIf(cond(this))

/**
 * Make nullable [MutableStateFlow] `null`
 * @receiver [MutableStateFlow] to make `null`
 */
fun <T> MutableStateFlow<T?>.nullate() {
	this.value = null
}

/**
 * Make nullable [MutableState] `null`
 * @receiver [MutableState] to make `null`
 */
fun <T> MutableState<T?>.nullate() {
	this.value = null
}
