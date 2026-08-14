package tr.qonferencer.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tr.qonferencer.nfc.ScanNfc
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation

@Composable
fun NfcLoginScreen(onDecode: (String) -> Unit) {
	ScanNfc(onDecode = onDecode)

	Box(
		modifier = Modifier.fillMaxWidth(),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = dynamicTranslation("login.nfc.scanning"),
			style = typo.bodyLarge,
		)
	}
}
