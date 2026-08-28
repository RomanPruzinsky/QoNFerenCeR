package tr.qonferencer.nfc

/** On/off switch for NFC HCE */
object NfcEmitter {
	private class Session(
		val id: Any,
		val tokenSupplier: () -> String?,
	)

	@Volatile
	private var session: Session? = null

	val isActive: Boolean get() = session != null

	fun start(
		id: Any,
		tokenSupplier: () -> String?,
	) {
		session = Session(id, tokenSupplier)
	}

	fun stop(id: Any) {
		if (session?.id === id) session = null
	}

	fun currentToken(): String? = session?.tokenSupplier?.invoke()
}
