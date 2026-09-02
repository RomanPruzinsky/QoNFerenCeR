package tr.qonferencer.trons.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tr.qonferencer.trons.theme.Edge.Companion.getBottoms
import tr.qonferencer.trons.theme.Edge.Companion.getEnds
import tr.qonferencer.trons.theme.Edge.Companion.getStarts
import tr.qonferencer.trons.theme.Edge.Companion.getTops

/**
 * Averages [edge1] and [edge2] to [CornerSize]
 * @param edge1 [Dp] to average
 * @param edge2 [Dp] to average
 * @return [CornerSize] of average of [edge1] and [edge2]
 */
private fun evalCornerSize(
	edge1: Dp,
	edge2: Dp,
): CornerSize = CornerSize((edge1 + edge2) / 2)

/**
 * Clips element with specific [Edge] and [Dp]
 * @param clips Pairs of <[Edge], [Dp]> to apply
 * @return Applied [Modifier]
 */
fun Modifier.specClip(vararg clips: Pair<Edge, Dp> = (arrayOf(Edge.ALL to defaultClipSize))) = this.then(
	Modifier.clip(
		CircleShape.copy(
			topStart = evalCornerSize(clips.getTops(), clips.getStarts()),
			topEnd = evalCornerSize(clips.getTops(), clips.getEnds()),
			bottomStart = evalCornerSize(clips.getBottoms(), clips.getStarts()),
			bottomEnd = evalCornerSize(clips.getBottoms(), clips.getEnds()),
		),
	),
)

/**
 * Defaultly styled [Modifier.clip]
 *
 * First multiplies [defaultClipSize], then adds [additier]
 *
 * @param clip [Edge] to clip
 * @param multiplier Multiplier for [defaultClipSize]
 * @param additier Specific [Dp] to add to [defaultClipSize] (after multiplied)
 * @return [Modifier] with applied clipping
 */
fun Modifier.defaultClip(
	clip: Edge = Edge.ALL,
	multiplier: Float = 1F,
	additier: Dp = 0.dp,
) = this.then(
	Modifier.specClip(
		clip to (defaultClipSize * multiplier) + additier,
	),
)

/**
 * Default [Edge.TOP] clip
 *
 * First multiplies [defaultClipSize], then adds [additier]
 *
 * @param multiplier Multiplier for [defaultClipSize]
 * @param additier Specific [Dp] to add to [defaultClipSize] (after multiplied)
 * @return [Modifier] with applied [Edge.TOP] clipping
 */
fun Modifier.defaultTopClip(
	multiplier: Float = 1F,
	additier: Dp = 0.dp,
) = this.then(
	Modifier.specClip(
		Edge.TOP to (defaultClipSize * multiplier) + additier,
	),
)

/**
 * Default [Edge.START] clip
 *
 * First multiplies [defaultClipSize], then adds [additier]
 *
 * @param multiplier Multiplier for [defaultClipSize]
 * @param additier Specific [Dp] to add to [defaultClipSize] (after multiplied)
 * @return [Modifier] with applied [Edge.START] clipping
 */
fun Modifier.defaultStartClip(
	multiplier: Float = 1F,
	additier: Dp = 0.dp,
) = this.then(
	Modifier.specClip(
		Edge.START to (defaultClipSize * multiplier) + additier,
	),
)

/**
 * Default [Edge.END] clip
 *
 * First multiplies [defaultClipSize], then adds [additier]
 *
 * @param multiplier Multiplier for [defaultClipSize]
 * @param additier Specific [Dp] to add to [defaultClipSize] (after multiplied)
 * @return [Modifier] with applied [Edge.END] clipping
 */
fun Modifier.defaultEndClip(
	multiplier: Float = 1F,
	additier: Dp = 0.dp,
) = this.then(
	Modifier.specClip(
		Edge.END to (defaultClipSize * multiplier) + additier,
	),
)

/**
 * Default [Edge.BOTTOM] clip
 *
 * First multiplies [defaultClipSize], then adds [additier]
 *
 * @param multiplier Multiplier for [defaultClipSize]
 * @param additier Specific [Dp] to add to [defaultClipSize] (after multiplied)
 * @return [Modifier] with applied [Edge.BOTTOM] clipping
 */
fun Modifier.defaultBottomClip(
	multiplier: Float = 1F,
	additier: Dp = 0.dp,
) = this.then(
	Modifier.specClip(
		Edge.BOTTOM to (defaultClipSize * multiplier) + additier,
	),
)
