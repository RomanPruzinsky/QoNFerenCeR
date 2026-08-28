package tr.qonferencer.qr

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import qrcode.QRCode
import qrcode.color.Colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.DialogFullWidth
import tr.qonferencer.trons.screen.KeepScreenOn
import tr.qonferencer.trons.screen.MaximizeBrightness
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

/** Fixed QR colors, ignoring app theme */
private object QrColors {
	const val FOREGROUND = Colors.BLACK
	const val BACKGROUND = Colors.WHITE
}

/** @return [this] rendered as QR code */
private fun String.toQrImageBitmap(): ImageBitmap {
	val bytes = QRCode
		.ofRoundedSquares()
		.withColor(QrColors.FOREGROUND)
		.withBackgroundColor(QrColors.BACKGROUND)
		.build(this)
		.render()
		.getBytes()
	return BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
}

/** If [opened]: shows [DialogFullWidth] with [qrData] as QR code and [intro] as additional text */
@Composable
fun ShowQrDialog(
	opened: MutableState<Boolean>,
	qrData: String,
	intro: String? = null,
) {
	if (!opened.value) return
	KeepScreenOn()
	MaximizeBrightness()

	DialogFullWidth(onDismissRequestAction = { opened.value = false }) {
		CardLayout {
			if (!intro.isNullOrBlank()) {
				Text(
					text = intro,
					style = typo.displayMedium,
					modifier = Modifier.defaultTextPadding(),
				)
			}

			val bitmap = remember(qrData) { qrData.toQrImageBitmap() }
			Image(
				bitmap = bitmap,
				contentDescription = qrData,
				modifier = Modifier
					.defaultLayoutPadding()
					.fillMaxWidth()
					.aspectRatio(1F)
					.defaultClip()
					.background(Color(QrColors.BACKGROUND))
					.defaultLayoutPadding(),
			)
		}
	}
}
