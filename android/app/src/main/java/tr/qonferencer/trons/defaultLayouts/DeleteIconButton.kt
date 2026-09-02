package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tr.qonferencer.theme.colors
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultTextPadding

/**
 * Defaultly styled delete-action icon, shared across admin editors
 * @param contentDescription Describes what gets deleted
 * @param modifier [Modifier] applied to parent, e.g. for `Alignment.End` or extra padding
 * @param onClick Delete action
 */
@Composable
fun DeleteIconButton(
	contentDescription: String,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	Icon(
		imageVector = Icons.Default.Delete,
		contentDescription = contentDescription,
		tint = colors.text,
		modifier = modifier
			.defaultClip()
			.background(colors.action.delete)
			.clickable(onClick = onClick)
			.defaultTextPadding()
			.size(defaultIconSizeLarge),
	)
}
