package tr.qonferencer.trons.theme

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tr.qonferencer.theme.colors

/** Basic animation for any element */
fun Modifier.defaultAnimation() = this.then(
	Modifier.animateContentSize(
		animationSpec = spring(
			dampingRatio = Spring.DampingRatioLowBouncy,
			stiffness = Spring.StiffnessLow,
		),
	),
)

/**
 * Alphaize element if [condition]
 * @param condition Whether to apply [alpha]
 * @param alpha Alpha value
 */
fun Modifier.alphaIf(condition: Boolean, alpha: Float = DEFAULT_HIDDEN_ALPHA) =
	this.then(Modifier.alpha(if (condition) alpha else 1F))

////////////////////////////////////
/////////// OPTIONABLE /////////////

/**
 * Optionable [Modifier] to apply if [cond] is `true`
 * @param ifTrue [Modifier] to apply
 * @param cond Condition to eval
 */
fun Modifier.optionable(ifTrue: Modifier, cond: () -> Boolean) = this.then(
	if (cond()) ifTrue
	else Modifier,
)

/**
 * Optionable [Modifier] to apply if [cond] is `true`
 * @param ifTrue [Modifier] to apply
 * @param cond Condition to eval
 */
fun Modifier.optionable(ifTrue: Modifier, cond: Boolean) = this.optionable(ifTrue) { cond }

/////////// OPTIONABLE /////////////
////////////////////////////////////
/////////// CLICKABLE //////////////

/**
 * [Modifier.clickable] with no indication and no ripple/animation.
 *
 * Skips allocating a `MutableInteractionSource` (passes `null` since there's no [indication]).
 *
 * @param onClick Action to execute on click
 */
fun Modifier.rawClickable(onClick: () -> Unit) = this.clickable(
	interactionSource = null,
	indication = null,
	onClick = onClick,
)

/**
 * [Modifier.clickable] but for long click
 * @param onLongClick Action to execute on long click
 */
fun Modifier.longClickable(onLongClick: () -> Unit) =
	this.pointerInput(Unit) { detectTapGestures(onLongPress = { onLongClick() }) }

/////////// CLICKABLE //////////////
////////////////////////////////////
//////////// BORDER ////////////////

/**
 * Basic clippeded border
 * - [CircleShape] with [defaultClipSize] corners
 * - Modifyable [color] of border line
 * - Modifyable [size] of border line
 *
 * @param should Whether to borderize or not
 * @param size Size of border line
 * @param color Color of border line
 */
@Composable
fun Modifier.defaultBorder(should: Boolean = true, size: Dp = defaultBorderSize, color: Color = colors.text) =
	this.optionable(
		ifTrue = Modifier
			.defaultClip()
			.border(
				width = size,
				shape = CircleShape.copy(CornerSize(defaultClipSize)),
				color = color,
			),
		cond = should,
	)

/**
 * Creates animated border
 * @param dashLength Length of dash
 * @param gapLength Length of gap
 * @param dashColor Color of dash
 * @param gapColor Color of gap
 * @param width Width of border
 * @param speed Speed of animation
 */
@Composable
fun Modifier.antsBorder(
	dashLength: Float = 30f,
	gapLength: Float = 20f,
	dashColor: Color = colors.selected,
	gapColor: Color = colors.element,
	width: Dp = 3.dp,
	speed: Int = 800,
): Modifier {
	val infiniteTransition = rememberInfiniteTransition()
	val phase by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = dashLength + gapLength,
		animationSpec = infiniteRepeatable(
			animation = tween(speed, easing = LinearEasing),
		),
	)
	
	return this
		.defaultClip()
		.drawWithContent {
			drawContent()
			val strokePx = width.toPx()
			val halfStroke = strokePx / 2f
			val radius = CornerRadius(defaultClipSize.toPx())
			val topLeft = Offset(halfStroke, halfStroke)
			val rectSize = Size(size.width - strokePx, size.height - strokePx)
			
			drawRoundRect(
				color = gapColor,
				topLeft = topLeft,
				size = rectSize,
				cornerRadius = radius,
				style = Stroke(width = strokePx),
			)
			
			drawRoundRect(
				color = dashColor,
				topLeft = topLeft,
				size = rectSize,
				cornerRadius = radius,
				style = Stroke(
					width = strokePx,
					pathEffect = PathEffect.dashPathEffect(
						floatArrayOf(dashLength, gapLength),
						phase,
					),
				),
			)
		}
}

//////////// BORDER ////////////////
////////////////////////////////////
