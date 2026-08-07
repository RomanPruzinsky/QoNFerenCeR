package tr.qonferencer.trons.ops

import androidx.compose.runtime.Composable

//////////////////////////////////
///////////// SINGLE /////////////

/**
 * If [List] is not empty, execute composable [body]
 * @receiver [List] to check of emptiness
 * @param body Action to execute if [this] is not empty, arguement is [List] receiver
 * @return `true` if [this] is not empty, `false` otherwise
 */
@Composable
fun <T> List<T>.ifNotEmpty(body: @Composable (List<T>) -> Unit): Boolean = if (isNotEmpty()) {
	body(this)
	true
} else false

/**
 * If [List] is not empty, execute [body]
 * @receiver [List] to check of emptiness
 * @param body Action to execute if [this] is not empty, arguement is [List] receiver
 * @return `true` if [this] is not empty, `false` otherwise
 */
fun <T> List<T>.ifNotEmptyRaw(body: (List<T>) -> Unit): Boolean = if (isNotEmpty()) {
	body(this)
	true
} else false

///////////// SINGLE /////////////
//////////////////////////////////
////////////// LOOP //////////////

/**
 * If [List] is not empty, execute composable [body] for each item
 * @receiver [List] to check of emptiness
 * @param body Action to execute if [this] is not empty, arguement is [T] item from receiver
 * @return `true` if [this] is not empty, `false` otherwise
 */
@Composable
fun <T> List<T>.ifNotEmptyLoop(body: @Composable (T) -> Unit): Boolean = if (isNotEmpty()) {
	this.forEach { body(it) }
	true
} else false

/**
 * If [List] is not empty, execute [body] for each item
 * @receiver [List] to check of emptiness
 * @param body Action to execute if [this] is not empty, arguement is [T] item from receiver
 * @return `true` if [this] is not empty, `false` otherwise
 */
fun <T> List<T>.ifNotEmptyLoopRaw(body: (T) -> Unit): Boolean = if (isNotEmpty()) {
	this.forEach { body(it) }
	true
} else false

////////////// LOOP //////////////
//////////////////////////////////
////////////// MISC //////////////

/**
 * Converts receiver [List] to new [List] with transformed items
 * @receiver [List] to transform
 * @param transform Transformation to apply to each item
 * @return new [List] with transformed items
 */
fun <C, T> List<C>.relist(transform: (C) -> T): List<T> = this.map(transform)

/**
 * Converts receiver [List] to new [List] with composably transformed items
 * @receiver [List] to composably transform
 * @param transform Composable transformation to apply to each item
 * @return new [List] with composably transformed items
 */
@Composable
fun <C, T> List<C>.relistC(transform: @Composable (C) -> T): List<T> = this.map { transform(it) }

/**
 * Basic [kotlin.text.contains] but with more items
 * @receiver [String] to search in
 * @param items Items to search for
 * @return `true` if [String] contains at least 1 of [items], `false` otherwise
 */
fun String.contains(vararg items: String): Boolean = items.any { this.contains(it, ignoreCase = false) }

/**
 * Whether [List] contains at least 1 of [containees]
 * @receiver [List] to search in
 * @param containees Items to search for
 * @return `true` if [List] contains at least 1 of [containees], `false` otherwise
 */
fun <T> List<T>.containsAny(containees: List<T>): Boolean {
	containees.forEach { if (this.contains(it)) return true }
	return false
}

/**
 * If [MutableList] contains [element], removes it, otherwise adds it.
 *
 * Works for `SnapshotStateList<T>` too (it is a `MutableList<T>`).
 *
 * @receiver [MutableList] to toggle in
 * @param element Element to toggle
 */
fun <T> MutableList<T>.toggleInList(element: T) {
	if (this.contains(element)) this.remove(element)
	else this.add(element)
}

/**
 * Finds index of first found item or null
 * @receiver List to search in
 * @param predicate What to search for
 * @return Index of first found item or null
 */
fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
	val index = this.indexOfFirst(predicate)
	return if (index == -1) null else index
}

////////////// MISC //////////////
//////////////////////////////////
