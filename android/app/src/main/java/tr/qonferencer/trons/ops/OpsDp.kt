package tr.qonferencer.trons.ops

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

/** Converts [Int] (usually pixels) to [Dp] */
@Composable
fun Int.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }
