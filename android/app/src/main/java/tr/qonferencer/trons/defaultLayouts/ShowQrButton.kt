package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tr.qonferencer.theme.colors
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultTextPadding

/**
 * Opens QR dialog
 * @param onClick Shows dialog
 */
@Composable
fun ShowQrButton(onClick: () -> Unit) {
	Icon(
		imageVector = Icons.Default.QrCode2,
		contentDescription = "show QR code",
		tint = colors.text,
		modifier = Modifier
			.defaultClip()
			.background(colors.clickable)
			.clickable(onClick = onClick)
			.defaultTextPadding(2F)
			.size(defaultIconSizeLarge),
	)
}
