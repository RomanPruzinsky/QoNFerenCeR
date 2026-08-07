package tr.qonferencer.trons.ops

import androidx.compose.runtime.Composable

/** Converts [Int] to [Boolean] */
fun Int.toBool() = this == 1

/** Converts [Boolean] to [Int] */
fun Boolean.toInt() = if (this) 1 else 0

/**
 * If [Boolean] is `false`, execute [body]
 * @receiver [Boolean] to eval
 * @param body Action to execute if [Boolean] is `false`
 */
@Composable
fun Boolean.ifNot(body: @Composable () -> Unit) {
	if (!this) body()
}

/**
 * Executes [this] if [bool] is `false`
 * @receiver Body to execute if [bool] is `false`
 * @param bool Condition to eval
 */
@Composable
fun (@Composable () -> Unit).ifNot(bool: Boolean) {
	if (!bool) this()
}
