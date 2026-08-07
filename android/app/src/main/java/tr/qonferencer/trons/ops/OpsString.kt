package tr.qonferencer.trons.ops

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.toUpperCase
import tr.qonferencer.trons.miscs.EMPTY_STRING
import java.text.Normalizer

///////////////////////////////////////////////
//////////////////// EMPTY ////////////////////

/**
 * If [CharSequence] is not empty, execute [body]
 * @receiver [CharSequence] to check emptiness
 * @param body Action to execute if [CharSequence] is not empty
 * @return `true` if [CharSequence] is not empty, `false` otherwise
 */
@Composable
fun CharSequence.ifNotEmpty(body: @Composable () -> Unit): Boolean = if (isNotEmpty()) {
	body()
	true
} else false

/**
 * If [CharSequence] is not empty, execute [body]
 * @receiver [CharSequence] to check emptiness
 * @param body Action to execute if [CharSequence] is not empty
 * @return `true` if [CharSequence] is not empty, `false` otherwise
 */
fun CharSequence.ifNotEmptyRaw(body: () -> Unit): Boolean = if (isNotEmpty()) {
	body()
	true
} else false

/**
 * Returns [EMPTY_STRING] if [statement] is `true`, [this] otherwise
 * @receiver [String] to return if [statement] if `false`
 * @param statement Condition to check
 * @return [EMPTY_STRING] if [statement] is `true`, [this] otherwise
 */
fun String.orEmptyIf(statement: Boolean) = if (statement) EMPTY_STRING else this

/**
 * Returns [EMPTY_STRING] if [statement] is `true`, [this] otherwise
 * @receiver [String] to return if [statement] if `false`
 * @param statement Condition to check, arguement is [String] receiver
 * @return [EMPTY_STRING] if [statement] is `true`, [this] otherwise
 */
fun String.orEmptyIf(statement: (String) -> Boolean) = this.orEmptyIf(statement(this))

/**
 * Returns [EMPTY_STRING] if receiver is `null`, [this] otherwise
 * @receiver [String] to return if not `null`
 * @return [EMPTY_STRING] if receiver is `null`, [this] otherwise
 */
fun String?.orEmptyIfNull(): String = this ?: EMPTY_STRING

//////////////////// EMPTY ////////////////////
///////////////////////////////////////////////
//////////////////// !NULL ////////////////////

/**
 * If [String] is not null, execute composable [body]
 * @receiver [String] to check of nullability
 * @param body Composable action to execute if [String] is not null
 */
@Composable
fun String?.IfNotNull(body: @Composable (String) -> Unit) {
	if (this != null) body(this)
}

/**
 * If [String] is not null, execute [body]
 * @receiver [String] to check of nullability
 * @param body Action to execute if [String] is not null
 */
fun String?.ifNotNull(body: (String) -> Unit) {
	if (this != null) body(this)
}

/**
 * If [String] is not null and not empty, execute composable [body]
 * @receiver [String] to check of nullability and emptiness
 * @param body Composable action to execute if [String] is not null and not empty
 */
@Composable
fun String?.IfNotNullNorEmpty(body: @Composable (String) -> Unit) {
	if (!this.isNullOrEmpty()) body(this)
}

/**
 * If [String] is not null and not empty, execute [body]
 * @receiver [String] to check of nullability and emptiness
 * @param body Action to execute if [String] is not null and not empty
 */
fun String?.ifNotNullNorEmpty(body: (String) -> Unit) {
	if (!this.isNullOrEmpty()) body(this)
}

//////////////////// !NULL ////////////////////
///////////////////////////////////////////////
///////////////////// MISC ////////////////////

/**
 * Removes diacritics from string
 * @receiver [String] to remove diacritics from
 * @return [String] without diacritics
 */
fun String.unaccent(): String = Normalizer.normalize(this, Normalizer.Form.NFD)
	.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), EMPTY_STRING)

/**
 * Converts string to lowercase based by [Locale.current]
 * @receiver [String] to lowercase
 */
fun String.lowered() = this.toLowerCase(Locale.current)

/**
 * Converts string to uppercase based by [Locale.current]
 * @receiver [String] to lowercase
 */
fun String.uppered() = this.toUpperCase(Locale.current)

///////////////////// MISC ////////////////////
///////////////////////////////////////////////
