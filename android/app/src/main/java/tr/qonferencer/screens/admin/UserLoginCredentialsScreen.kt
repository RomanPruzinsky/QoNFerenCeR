package tr.qonferencer.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import tr.qonferencer.api.formatAsLoginQrJson
import tr.qonferencer.nfc.emitNfc
import tr.qonferencer.nfc.rememberIsNfcHceSupported
import tr.qonferencer.qr.ShowQrDialog
import tr.qonferencer.shared.dtos.LoginCredentialsDto
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.translations.rawDynamicTranslation
import tr.qonferencer.trons.defaultLayouts.DefaultHeightSpacer
import tr.qonferencer.trons.defaultLayouts.IconIndicator
import tr.qonferencer.trons.defaultLayouts.ProfileDisplayRow
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.ShowQrButton
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.setClipboardTextWithNotification
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.theme.Edge
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding
import tr.qonferencer.trons.theme.specPadding

/**
 * Full-screen reveal of [credentials] for slot named [fullName] - QR, NFC and copyable rows
 * @param doneLabel Text of closing action
 * @param onDone Called on closing action
 */
@Composable
fun UserLoginCredentialsScreen(
	fullName: String,
	credentials: LoginCredentialsDto,
	doneLabel: String,
	onDone: () -> Unit,
) {
	val showQr = rememberFalse()
	val clipboard = LocalClipboard.current
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	val copiedText = dynamicTranslation("misc.copied")
	val qrData = remember(credentials) { credentials.formatAsLoginQrJson() }
	emitNfc { qrData }
	
	ShowQrDialog(opened = showQr, qrData = qrData, intro = fullName)
	
	ScrollableColumn(
		modifier = Modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
		verticalArrangement = defaultSpacing,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(
			text = fullName,
			style = typo.displayLarge,
		)
		
		DefaultHeightSpacer()
		
		mapOf(
			"login.manual.username" to credentials.username,
			"login.manual.password" to credentials.password,
		).forEach { (label, value) ->
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.defaultLayoutPadding()
					.defaultClip()
					.background(colors.container)
					.clickable {
						setClipboardTextWithNotification(
							clipboardTitle = rawDynamicTranslation(label),
							textToCopy = value,
							textIndicator = copiedText,
							clipboard = clipboard,
							context = context,
							scope = scope,
						)
					}
					.defaultLayoutPadding(),
			) {
				IconIndicator {
					Box(modifier = Modifier.specPadding(Edge.END to defaultIconSizeLarge)) {
						ProfileDisplayRow(dynamicTranslation(label), value)
					}
				}
			}
		}
		
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Box(modifier = Modifier.weight(1F)) {
				if (rememberIsNfcHceSupported()) {
					Text(
						text = dynamicTranslation("user.detail.emittingNfc"),
						style = typo.bodySmall,
					)
				} else Spacer(Modifier.fillMaxWidth())
			}
			
			ShowQrButton { showQr.value = true }
		}
		
		Spacer(modifier = Modifier.weight(1F))
		
		Text(
			text = doneLabel,
			style = typo.labelLarge,
			modifier = Modifier
				.align(Alignment.End)
				.defaultClip()
				.background(colors.clickable)
				.clickable(onClick = onDone)
				.defaultTextPadding(),
		)
	}
}
