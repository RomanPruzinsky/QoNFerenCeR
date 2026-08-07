package tr.qonferencer.trons.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Composable state of [Dp] with `0` as initial value
 * @return [MutableState] of [Dp] with `0` value
 */
@Composable
fun remember0dp(): MutableState<Dp> = remember { mutableStateOf(0.dp) }
