package tr.qonferencer.trons.theme

import androidx.compose.ui.graphics.Color

/**
 * Gets [Color.Transparent] if [b] is `true`, else receiver
 * @receiver [Color] to return if [b] is `true`
 * @param b [Boolean] to eval
 * @return [Color.Transparent] if [b] is `true`, else receiver
 */
fun Color.orTransparentIf(b: Boolean) = if (b) Color.Transparent else this

/**
 * Gets receiver if [b] is `true`, else [Color.Transparent]
 * @receiver [Color] to return if [b] is `true`
 * @param b [Boolean] to eval
 * @return receiver if [b] is `true`, else [Color.Transparent]
 */
fun Color.elseTransparent(b: Boolean) = if (b) this else Color.Transparent
