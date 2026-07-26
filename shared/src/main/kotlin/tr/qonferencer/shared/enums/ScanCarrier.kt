package tr.qonferencer.shared.enums

/** How a scan was captured, declared by the phone and checked against the token's strength */
enum class ScanCarrier {
	/** Rotating token read off the screen with a camera */
	QR,

	/** Rotating token received over an NFC tap */
	NFC,

	/** Printed static badge number */
	BARCODE,

	/** A human resolved the identity: badge number typed in, or picked from a name search */
	MANUAL,
	;

	/** Whether this carrier delivers the rotating per-scan token rather than a static id */
	val isRotating: Boolean get() = this == QR || this == NFC
}
