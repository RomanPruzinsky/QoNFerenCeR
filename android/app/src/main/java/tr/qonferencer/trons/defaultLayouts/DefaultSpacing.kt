package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import tr.qonferencer.trons.theme.defaultLayoutPadding

/**
 * Applied as [Arrangement] in [Row] or [Column]
 *
 * Use when want to [Arrangement.spacedBy] be [defaultLayoutPadding]
 */
val defaultSpacing = Arrangement.spacedBy(defaultLayoutPadding)

/**
 * Applied as [Alignment.Horizontal] in [Row] or [Column]
 *
 * Use when want to [Arrangement.spacedBy] be [defaultLayoutPadding] with specific [Alignment]
 *
 * @param align [Alignment.Horizontal] of content
 */
fun defaultHorizontalSpacing(align: Alignment.Horizontal = Alignment.CenterHorizontally) = Arrangement.spacedBy(
	space = defaultLayoutPadding,
	alignment = align,
)

/**
 * Applied as [Alignment.Vertical] in [Row] or [Column]
 *
 * Use when want to [Arrangement.spacedBy] be [defaultLayoutPadding] with specific [Alignment]
 *
 * @param align [Alignment.Vertical] of content
 */
fun defaultVerticalSpacing(align: Alignment.Vertical = Alignment.CenterVertically) = Arrangement.spacedBy(
	space = defaultLayoutPadding,
	alignment = align,
)
