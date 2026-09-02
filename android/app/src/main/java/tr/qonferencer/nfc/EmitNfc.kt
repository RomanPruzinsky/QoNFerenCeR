package tr.qonferencer.nfc

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.api.auth.AuthTokenHelper
import tr.qonferencer.shared.scan.ScanToken
import tr.qonferencer.trons.miscs.delaySeconds
import java.time.Instant
import java.util.Base64

/** @return Current rotating meal token, or `null` if not logged in / meal secret unavailable */
fun currentRotatingToken(): String? {
	val userId = QoNFerenCeRApp.currentUser.details.value?.userId ?: return null
	val secret = AuthTokenHelper(QoNFerenCeRApp.tokenStore).mealSecret()
		?.let { Base64.getDecoder().decode(it) }
		?: return null
	return ScanToken.build(userId, secret, Instant.now().epochSecond)
}

/**
 * Emits [payload] via NFC while composed
 * @param payload What to emit
 * @return Value currently being emitted, refreshed every [TOKEN_REFRESH_SECONDS]
 */
@Composable
fun emitNfc(payload: () -> String?): String? {
	val payloadUpdated = rememberUpdatedState(payload)

	DisposableEffect(Unit) {
		val id = Any()
		NfcEmitter.start(id) { payloadUpdated.value() }
		onDispose { NfcEmitter.stop(id) }
	}
	
	val token by produceState(initialValue = payloadUpdated.value()) {
		while (true) {
			delaySeconds(TOKEN_REFRESH_SECONDS)
			value = payloadUpdated.value()
		}
	}
	return token
}

/** How often [emitNfc] recomputes token */
private const val TOKEN_REFRESH_SECONDS = 10

/** @return Whether device hardware supports NFC Host Card Emulation */
@Composable
fun rememberIsNfcHceSupported(): Boolean {
	val context = LocalContext.current
	return remember { context.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION) }
}
