package tr.qonferencer.shared.enums

/** How a scan was captured */
enum class ScannerType {
	QR,
	NFC,
	BARCODE,
	MANUAL,
	;

	/** Whether scan is **rotating** or **static** */
	val isRotating: Boolean get() = this == QR || this == NFC
}
