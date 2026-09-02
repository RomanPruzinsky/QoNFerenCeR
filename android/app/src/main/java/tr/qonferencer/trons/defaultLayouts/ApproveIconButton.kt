package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tr.qonferencer.theme.colors
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultTextPadding

/** Defaultly styled approve-action icon */
@Composable
fun ApproveIconButton(
	contentDescription: String,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	Icon(
		imageVector = Icons.Default.Check,
		contentDescription = contentDescription,
		tint = colors.text,
		modifier = modifier
			.defaultClip()
			.background(colors.action.approve)
			.clickable(onClick = onClick)
			.defaultTextPadding()
			.size(defaultIconSizeLarge),
	)
}
