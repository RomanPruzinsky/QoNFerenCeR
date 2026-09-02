package tr.qonferencer.trons.miscs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Application-wide UI coroutine scope.
 *
 * Uses a [SupervisorJob] so a failure in one launched coroutine does not
 * cancel sibling coroutines or the whole scope.
 */
val mainUICoroutine: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
