package tr.qonferencer.trons.states

import androidx.compose.runtime.Composable
import tr.qonferencer.trons.miscs.ENDL

/** Default message to be printed if no exception message provided */
var errorMessage: String = StateSay.ERROR

/**
 * Error message
 *
 * Class name of [Exception] or [errorMessage] if no Exception provided
 *
 * @param specificClass Trigerred exception to extract class name from
 * @return Class name of [specificClass] or [errorMessage]
 */
fun getError(specificClass: Exception? = null): String = specificClass?.javaClass?.toString()?.run {
	specificClass.printStackTrace()
	this.substring(this.lastIndexOf(".") + 1)
} ?: errorMessage

/**
 * String with [StateSay.ERROR] and [e] message
 * @param e Exception to get info from
 * @see [getError]
 */
@Composable
fun errorIndicatorMessage(e: Exception? = null) = StateSay.ERROR + ENDL + getError(e)
