package tr.qonferencer.nfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.shared.scan.ScanToken
import java.nio.ByteBuffer

/** HCE card for AID "QoNFerenCeR", answers only while [NfcEmitter] has active screen */
class QoNFerenCeRHceService : HostApduService() {

	override fun processCommandApdu(apdu: ByteArray?, extras: Bundle?): ByteArray {
		if (apdu == null) return SW_UNKNOWN
		return try {
			when {
				apdu.startsWith(SELECT_AID_HEADER) -> handleSelectAid()
				apdu.startsWith(GET_DATA_HEADER) -> handleGetData()
				else -> SW_INS_NOT_SUPPORTED
			}
		} catch (_: Exception) {
			SW_UNKNOWN
		}
	}

	override fun onDeactivated(reason: Int) = Unit

	private fun handleSelectAid(): ByteArray {
		val userId = QoNFerenCeRApp.currentUser.details.value?.userId
		if (!NfcEmitter.isActive || userId == null) return SW_FILE_NOT_FOUND
		val payload = ByteBuffer
			.allocate(Long.SIZE_BYTES + 1)
			.putLong(userId)
			.put(ScanToken.TOKEN_VERSION.toByte())
			.array()
		return payload + SW_OK
	}

	private fun handleGetData(): ByteArray {
		val token = NfcEmitter.currentToken()?.toByteArray(Charsets.US_ASCII)
			?: return SW_CONDITIONS_NOT_SATISFIED
		return token + SW_OK
	}

	private companion object {
		val SELECT_AID_HEADER = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00)
		val GET_DATA_HEADER = byteArrayOf(0x00, 0xCA.toByte(), 0x00, 0x01)

		val SW_OK = byteArrayOf(0x90.toByte(), 0x00)
		val SW_FILE_NOT_FOUND = byteArrayOf(0x6A, 0x82.toByte())
		val SW_CONDITIONS_NOT_SATISFIED = byteArrayOf(0x69, 0x85.toByte())
		val SW_INS_NOT_SUPPORTED = byteArrayOf(0x6D, 0x00)
		val SW_UNKNOWN = byteArrayOf(0x6F, 0x00)

		fun ByteArray.startsWith(prefix: ByteArray): Boolean {
			if (size < prefix.size) return false
			for (i in prefix.indices) if (this[i] != prefix[i]) return false
			return true
		}
	}
}
