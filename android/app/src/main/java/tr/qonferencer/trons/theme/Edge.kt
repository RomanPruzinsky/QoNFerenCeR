package tr.qonferencer.trons.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tr.qonferencer.trons.theme.Edge.ALL
import tr.qonferencer.trons.theme.Edge.BOTTOM
import tr.qonferencer.trons.theme.Edge.BOTTOM_END
import tr.qonferencer.trons.theme.Edge.BOTTOM_START
import tr.qonferencer.trons.theme.Edge.END
import tr.qonferencer.trons.theme.Edge.HORIZONTAL
import tr.qonferencer.trons.theme.Edge.NONE
import tr.qonferencer.trons.theme.Edge.START
import tr.qonferencer.trons.theme.Edge.TOP
import tr.qonferencer.trons.theme.Edge.TOP_END
import tr.qonferencer.trons.theme.Edge.TOP_START
import tr.qonferencer.trons.theme.Edge.U_BOTTOM
import tr.qonferencer.trons.theme.Edge.U_END
import tr.qonferencer.trons.theme.Edge.U_START
import tr.qonferencer.trons.theme.Edge.U_TOP
import tr.qonferencer.trons.theme.Edge.VERTICAL

/**
 * Edge appliable in [specClip] or [specPadding]
 *
 * - [ALL] - all edges
 * ---
 * - [TOP] - top edge
 * - [BOTTOM] - bottom edge
 * - [START] - start edge
 * - [END] - end edge
 * ---
 * - [VERTICAL] - vertical edges
 * - [HORIZONTAL] - horizontal edges
 * ---
 * - [U_TOP] - U-shaped top edge => [TOP] + [HORIZONTAL]
 * - [U_BOTTOM] - U-shaped bottom edge => [BOTTOM] + [HORIZONTAL]
 * - [U_START] - U-shaped start edge => [START] + [VERTICAL]
 * - [U_END] - U-shaped end edge => [END] + [VERTICAL]
 * ---
 * - [TOP_START] - top-start corner => [TOP] + [START]
 * - [TOP_END] - top-end corner => [TOP] + [END]
 * - [BOTTOM_START] - bottom-start corner => [BOTTOM] + [START]
 * - [BOTTOM_END] - bottom-end corner => [BOTTOM] + [END]
 * ---
 * - [NONE] - no edging
 *
 * @see [specClip]
 * @see [specPadding]
 * @see [defaultClip]
 * @see [defaultLayoutPadding]
 */
enum class Edge {
	ALL,

	TOP,
	BOTTOM,
	START,
	END,

	VERTICAL,
	HORIZONTAL,

	U_TOP,
	U_BOTTOM,
	U_START,
	U_END,

	TOP_START,
	TOP_END,
	BOTTOM_START,
	BOTTOM_END,

	NONE,

	;

	companion object {
		/** Edges that modify [TOP] */
		private val topPads = setOf(ALL, TOP, VERTICAL, U_TOP, U_START, U_END, TOP_START, TOP_END)

		/** Edges that modify [BOTTOM] */
		private val bottomPads =
			setOf(ALL, BOTTOM, VERTICAL, U_BOTTOM, U_START, U_END, BOTTOM_START, BOTTOM_END)

		/** Edges that modify [START] */
		private val startPads =
			setOf(ALL, START, HORIZONTAL, U_TOP, U_BOTTOM, U_START, TOP_START, BOTTOM_START)

		/** Edges that modify [END] */
		private val endPads = setOf(ALL, END, HORIZONTAL, U_TOP, U_BOTTOM, U_END, TOP_END, BOTTOM_END)

		/**
		 * Sums value of specific [Edge] in receiver
		 * @receiver [Array] of [Pair] of [Edge]<[Edge], [Dp]>
		 * @param predicate [Edge] to sum
		 * @return [Dp] sum of specific [Edge]
		 */
		private fun Array<out Pair<Edge, Dp>>.getDpSumIf(predicate: (Edge) -> Boolean): Dp =
			this.filter { predicate(it.first) }.fold(0.dp) { sum, it -> sum + it.second }

		/**
		 * Sums all [Edge.TOP] from receiver
		 * @receiver [Array] of [Pair] of [Edge]<[Edge], [Dp]>
		 * @return [Dp] sum of [Edge.TOP]
		 */
		fun Array<out Pair<Edge, Dp>>.getTops() = this.getDpSumIf { it in topPads }

		/**
		 * Sums all [Edge.BOTTOM] from receiver
		 * @receiver [Array] of [Pair] of [Edge]<[Edge], [Dp]>
		 * @return [Dp] sum of [Edge.BOTTOM]
		 */
		fun Array<out Pair<Edge, Dp>>.getBottoms() = this.getDpSumIf { it in bottomPads }

		/**
		 * Sums all [Edge.START] from receiver
		 * @receiver [Array] of [Pair] of [Edge]<[Edge], [Dp]>
		 * @return [Dp] sum of [Edge.START]
		 */
		fun Array<out Pair<Edge, Dp>>.getStarts() = this.getDpSumIf { it in startPads }

		/**
		 * Sums all [Edge.END] from receiver
		 * @receiver [Array] of [Pair] of [Edge]<[Edge], [Dp]>
		 * @return [Dp] sum of [Edge.END]
		 */
		fun Array<out Pair<Edge, Dp>>.getEnds() = this.getDpSumIf { it in endPads }
	}
}
