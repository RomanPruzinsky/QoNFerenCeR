package tr.qonferencer.trons.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.theme.Edge.Companion.getBottoms
import tr.qonferencer.trons.theme.Edge.Companion.getEnds
import tr.qonferencer.trons.theme.Edge.Companion.getStarts
import tr.qonferencer.trons.theme.Edge.Companion.getTops

/**
 * Pads element with specific [Edge] and [Dp]
 * @param pads Pairs of <[Edge], [Dp]> to apply
 * @return Applied [Modifier]
 */
fun Modifier.specPadding(vararg pads: Pair<Edge, Dp> = arrayOf((Edge.ALL to defaultLayoutPadding))) = this.then(
	Modifier.padding(
		top = pads.getTops(),
		bottom = pads.getBottoms(),
		start = pads.getStarts(),
		end = pads.getEnds(),
	),
)

/**
 * Defaultly styled [Modifier.padding]
 *
 * First multiplies [defaultLayoutPadding] size, then adds [additier]
 *
 * @param pad [Edge] to pad
 * @param multiplier Multiplier for [defaultLayoutPadding]
 * @param additier Specific [Dp] to add to [defaultLayoutPadding] (after multiplied)
 * @return [Modifier] with applied padding
 */
fun Modifier.defaultLayoutPadding(pad: Edge = Edge.ALL, multiplier: Float = 1F, additier: Dp = 0.dp) = this.then(
	Modifier.specPadding(
		pad to (defaultLayoutPadding * multiplier) + additier,
	),
)

/**
 * Defaultly styled [Text] padding, modifiable with [multiplier]
 *
 * @param multiplier Multiplier for [defaultTextHorizontalPadding] and [defaultTextVerticalPadding]
 * @return [Modifier] with applied paddings
 */
fun Modifier.defaultTextPadding(multiplier: Float = 1F) = this.then(
	Modifier.specPadding(
		Edge.VERTICAL to defaultTextVerticalPadding * multiplier,
		Edge.HORIZONTAL to defaultTextHorizontalPadding * multiplier,
	),
)

/**
 * Defaultly styled [OutlinedTextField] padding, modifiable with [multiplier]
 *
 * @param multiplier Multiplier for [defaultOtfHorizontalPadding] and [defaultOtfVerticalPadding]
 * @return [Modifier] with applied paddings
 *
 * @see [DefaultOTF]
 */
fun Modifier.defaultOTFPadding(multiplier: Float = 1F) = this.then(
	Modifier.specPadding(
		Edge.VERTICAL to defaultOtfVerticalPadding * multiplier,
		Edge.HORIZONTAL to defaultOtfHorizontalPadding * multiplier,
	),
)
