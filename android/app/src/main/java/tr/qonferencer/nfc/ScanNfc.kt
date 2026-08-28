package tr.qonferencer.nfc

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import tr.qonferencer.R
import tr.qonferencer.trons.screen.findActivity

/** Scans NFC while composed */
@Composable
fun ScanNfc(onDecode: (String) -> Unit) {
	val context = LocalContext.current
	val aidHex = stringResource(R.string.qonferencer_aid)
	val selectAidApdu = remember(aidHex) { ApduConsts.buildSelectApdu(aidHex) }

	DisposableEffect(selectAidApdu, onDecode) {
		val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
		val adapter = NfcAdapter.getDefaultAdapter(activity)

		var hasDecoded = false
		val callback = NfcAdapter.ReaderCallback { tag ->
			if (!hasDecoded) {
				hasDecoded = true
				onDecode(readToken(tag, selectAidApdu) ?: "")
			}
		}

		adapter?.enableReaderMode(
			activity,
			callback,
			NfcAdapter.FLAG_READER_NFC_A,
			null,
		)
		onDispose { adapter?.disableReaderMode(activity) }
	}
}

/** @return Current token read from [tag], or `null` if unreadable */
private fun readToken(
	tag: Tag,
	selectAidApdu: ByteArray,
): String? {
	val isoDep = IsoDep.get(tag) ?: return null
	return runCatching {
		isoDep.use {
			it.connect()
			if (!ApduConsts.isOk(it.transceive(selectAidApdu))) return@use null
			val token = ApduConsts.stripOk(it.transceive(ApduConsts.GET_DATA_APDU)) ?: return@use null
			String(token, Charsets.US_ASCII)
		}
	}.getOrNull()
}
