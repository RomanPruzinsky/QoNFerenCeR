package tr.qonferencer.nfc

/** ISO/IEC 7816-4 APDU protocol, shared by [ScanNfc] (reader) and [QoNFerenCeRHceService] (emitter) */
object ApduConsts {
	/** Instruction class, standard/interindustry commands */
	const val CLA = 0x00

	/** SELECT instruction code */
	const val INS_SELECT = 0xA4

	/** GET DATA instruction code */
	const val INS_GET_DATA = 0xCA

	/** SELECT param: select by DF/application name */
	const val P1_SELECT_BY_NAME = 0x04

	/** SELECT param: first/only occurrence */
	const val P2_FIRST_OCCURRENCE = 0x00

	/** GET DATA param 1, unused by [QoNFerenCeRHceService] */
	const val P1_GET_DATA = 0x00

	/** GET DATA param 2, tag identifying requested data */
	const val P2_GET_DATA = 0x01

	/** Expected response length: unknown, return whatever's available */
	const val LE_ANY_LENGTH = 0x00

	/** ISO 7816-4 status word for success */
	val STATUS_WORD_OK = byteArrayOf(0x90.toByte(), 0x00)

	val SELECT_AID_HEADER = byteArrayOf(
		CLA.toByte(),
		INS_SELECT.toByte(),
		P1_SELECT_BY_NAME.toByte(),
		P2_FIRST_OCCURRENCE.toByte(),
	)
	val GET_DATA_HEADER = byteArrayOf(
		CLA.toByte(),
		INS_GET_DATA.toByte(),
		P1_GET_DATA.toByte(),
		P2_GET_DATA.toByte(),
	)
	val GET_DATA_APDU = GET_DATA_HEADER + LE_ANY_LENGTH.toByte()

	/** @return SELECT command for AID [aidHex] (reader side) */
	fun buildSelectApdu(aidHex: String): ByteArray {
		val aid = aidHex.hexToByteArray()
		return SELECT_AID_HEADER + aid.size.toByte() + aid + LE_ANY_LENGTH.toByte()
	}

	/** @return [data] with success status word appended */
	fun appendOk(data: ByteArray): ByteArray = data + STATUS_WORD_OK

	/** @return Whether [response] ends with success status word */
	fun isOk(response: ByteArray): Boolean = response.size >= STATUS_WORD_OK.size &&
		response[response.size - STATUS_WORD_OK.size] == STATUS_WORD_OK[0] &&
		response[response.size - STATUS_WORD_OK.size + 1] == STATUS_WORD_OK[1]

	/** @return [response]'s payload with status word stripped, or `null` if it isn't [isOk] */
	fun stripOk(response: ByteArray): ByteArray? {
		if (!isOk(response)) return null
		return response.copyOfRange(0, response.size - STATUS_WORD_OK.size)
	}
}
