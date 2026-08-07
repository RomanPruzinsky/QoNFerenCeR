package tr.qonferencer.trons.ops

import androidx.compose.runtime.Composable

//////////////////////////////////
///////////// SINGLE /////////////

/**
 * If [Map] is not empty, execute composable [body]
 * @receiver [Map] to check of emptiness
 * @param body Action to execute if [this] is not empty, arguement is [Map] receiver
 * @return `true` if [this] is not empty, `false` otherwise
 */
@Composable
fun <K, V> Map<K, V>.ifNotEmpty(body: @Composable (Map<K, V>) -> Unit): Boolean = if (this.isNotEmpty()) {
	body(this)
	true
} else false

/**
 * If [Map] is not empty, execute [body]
 * @receiver [Map] to check of emptiness
 * @param body Action to execute if [this] is not empty, arguement is [Map] receiver
 * @return `true` if [this] is not empty, `false` otherwise
 */
fun <K, V> Map<K, V>.ifNotEmptyRaw(body: (Map<K, V>) -> Unit): Boolean = if (this.isNotEmpty()) {
	body(this)
	true
} else false

///////////// SINGLE /////////////
//////////////////////////////////
////////////// LOOP //////////////

/**
 * If [Map] is not empty, execute composable [body] for each entry
 * @receiver [Map] to check of emptiness
 * @param body Action to execute if [this] is not empty, arguement is [Map.Entry] receiver
 * @return `true` if [this] is not empty, `false` otherwise
 */
@Composable
fun <K, V> Map<K, V>.ifNotEmptyLoop(body: @Composable (K, V) -> Unit): Boolean = if (isNotEmpty()) {
	this.forEach { body(it.key, it.value) }
	true
} else false

/**
 * If [Map] is not empty, execute [body] for each entry
 * @receiver [Map] to check of emptiness
 * @param body Action to execute if [this] is not empty, arguement is [Map.Entry] receiver
 * @return `true` if [this] is not empty, `false` otherwise
 */
fun <K, V> Map<K, V>.ifNotEmptyLoopRaw(body: (K, V) -> Unit): Boolean = if (isNotEmpty()) {
	this.forEach { body(it.key, it.value) }
	true
} else false

////////////// LOOP //////////////
//////////////////////////////////
////////////// MISC //////////////

/**
 * Clears [this] and puts new entry
 * @receiver [MutableMap] to clear and put new entry
 * @param key Key of new entry
 * @param value Value of new entry
 * @return [MutableMap] with 1 element <[key], [value]>
 */
fun <K, V> MutableMap<K, V>.putToEmpty(key: K, value: V): MutableMap<K, V> {
	this.clear()
	this[key] = value
	return this
}

////////////// MISC //////////////
//////////////////////////////////
