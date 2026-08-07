package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.Dp
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultBorder
import tr.qonferencer.trons.theme.defaultCardElevation
import tr.qonferencer.trons.theme.defaultClipSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextHorizontalPadding
import tr.qonferencer.trons.theme.defaultTextVerticalPadding
import tr.qonferencer.trons.theme.halfDefaultLayoutPadding
import tr.qonferencer.trons.theme.optionable
import tr.qonferencer.trons.theme.specPadding

/** Apply none padding to [CardLayout]:pads */
val PADS_NONE = null

/** Apply [defaultLayoutPadding] padding to [Edge.ALL] */
val PADS_DEFAULT = arrayOf(Edge.ALL to defaultLayoutPadding)

/** Apply [halfDefaultLayoutPadding] padding to [Edge.ALL] */
val PADS_DEFAULT_HALF = arrayOf(Edge.ALL to halfDefaultLayoutPadding)

/** Apply [halfDefaultLayoutPadding] padding to [Edge.ALL] */
val PADS_TEXT = arrayOf(
	Edge.HORIZONTAL to defaultTextHorizontalPadding,
	Edge.VERTICAL to defaultTextVerticalPadding,
)

/**
 * Display content inside defaultly-styled [Card]
 *
 * Body is stored inside [Column]
 *
 * @param modifier [Modifier] applied to parent
 * @param outerPads Optional array of [Pair]<[Edge], [Dp]> for padding around [Card]
 * @param innerPads Optional array of [Pair]<[Edge], [Dp]> for padding of [Card]'s body
 * @param borderize Bool indicating whether to apply [defaultBorder]
 * @param columnModifier [Modifier] applied to internal [Column] holding body
 * @param containerColor [Color] for [Card] background
 * @param contentHorizontalAlignment Horizontal alignment of [Column] holding body
 * @param contentVerticalArrangement Vertical arrangement of [Column] holding body
 * @param body [Card]'s content
 */
@Composable
fun CardLayout(
	modifier: Modifier = Modifier,
	columnModifier: Modifier = Modifier,
	outerPads: Array<Pair<Edge, Dp>>? = PADS_NONE,
	innerPads: Array<Pair<Edge, Dp>>? = PADS_DEFAULT,
	borderize: Boolean = false,
	containerColor: Color = Color.Unspecified,
	contentHorizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
	contentVerticalArrangement: Arrangement.Vertical = Arrangement.Top,
	body: @Composable ColumnScope.() -> Unit,
) {
	Card(
		shape = CircleShape.copy(CornerSize(defaultClipSize)),
		elevation = CardDefaults.cardElevation(defaultCardElevation),
		colors = CardDefaults.cardColors(containerColor = containerColor.takeOrElse { MaterialTheme.colorScheme.surface }),
		modifier = Modifier
			.then(
				if (outerPads != null) Modifier.specPadding(*outerPads)
				else Modifier,
			)
			.optionable(Modifier.defaultBorder(), borderize)
			.then(modifier),
	) {
		Column(
			modifier = columnModifier
				.then(
					if (innerPads != null) Modifier.specPadding(*innerPads)
					else Modifier,
				),
			horizontalAlignment = contentHorizontalAlignment,
			verticalArrangement = contentVerticalArrangement,
		) {
			body()
		}
	}
}

/**
 * ## Call [CardLayout] as item
 *
 * Display content inside defaultly-styled [Card]
 *
 * Body is stored inside [Column]
 *
 * @param modifier [Modifier] applied to parent
 * @param outerPads Optional array of [Pair]<[Edge], [Dp]> for padding around [Card]
 * @param innerPads Optional array of [Pair]<[Edge], [Dp]> for padding of [Card]'s body
 * @param borderize Bool indicating whether to apply [defaultBorder]
 * @param columnModifier [Modifier] applied to internal [Column] holding body
 * @param containerColor [Color] for [Card] background
 * @param contentHorizontalAlignment Horizontal alignment of [Column] holding body
 * @param contentVerticalArrangement Vertical arrangement of [Column] holding body
 * @param body [Card]'s content
 */
fun LazyListScope.cardLayoutItem(
	modifier: Modifier = Modifier,
	outerPads: Array<Pair<Edge, Dp>>? = PADS_NONE,
	innerPads: Array<Pair<Edge, Dp>>? = PADS_DEFAULT,
	borderize: Boolean = false,
	columnModifier: Modifier = Modifier,
	containerColor: Color = Color.Unspecified,
	contentHorizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
	contentVerticalArrangement: Arrangement.Vertical = Arrangement.Top,
	body: @Composable ColumnScope.() -> Unit,
) {
	item {
		CardLayout(
			modifier = modifier,
			outerPads = outerPads,
			innerPads = innerPads,
			borderize = borderize,
			columnModifier = columnModifier,
			containerColor = containerColor,
			contentHorizontalAlignment = contentHorizontalAlignment,
			contentVerticalArrangement = contentVerticalArrangement,
			body = body,
		)
	}
}
