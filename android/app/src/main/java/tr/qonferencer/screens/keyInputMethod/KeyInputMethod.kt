package tr.qonferencer.screens.keyInputMethod

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.ui.graphics.vector.ImageVector

/** Ways to read someone's identifying key: by camera, NFC, or manual entry */
enum class KeyInputMethod(
	val icon: ImageVector,
	val labelKey: String,
) {
	QR_BAR(Icons.Default.QrCode2, "keyInputMethod.qr_bar"),
	NFC(Icons.Default.Nfc, "keyInputMethod.nfc"),
	MANUAL(Icons.Default.Keyboard, "keyInputMethod.manual"),
}
