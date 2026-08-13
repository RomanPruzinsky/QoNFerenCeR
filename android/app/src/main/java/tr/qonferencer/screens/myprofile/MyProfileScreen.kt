package tr.qonferencer.screens.myprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.api.authRepository
import tr.qonferencer.nfc.emitNfc
import tr.qonferencer.nfc.rememberIsNfcHceSupported
import tr.qonferencer.qr.ShowQrDialog
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.CartedGroupBox
import tr.qonferencer.trons.defaultLayouts.DefaultHeightSpacer
import tr.qonferencer.trons.defaultLayouts.PADS_NONE
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.ops.orNullIf
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun MyProfileScreen(modifier: Modifier = Modifier) {
	val token = emitNfc()!!
	val user = QoNFerenCeRApp.currentUser.details.collectValue() ?: return
	val mealWindows = QoNFerenCeRApp.mealWindows.windows.collectValue()
	val coroutineScope = rememberCoroutineScope()

	ScrollableColumn(
		modifier = modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
		verticalArrangement = defaultSpacing,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(
			text = user.fullName,
			style = typo.displayLarge,
		)

		DefaultHeightSpacer(2)

		ProfileRow(dynamicTranslation("user.detail.role"), user.role.name)
		ProfileRow(dynamicTranslation("user.detail.userId"), user.userId.toString())

		ProfileRow(dynamicTranslation("user.detail.isSpeaker"), DefaultSay.yesOrNo(user.isSpeaker))
		ProfileRow(dynamicTranslation("user.detail.canCheckByName"), DefaultSay.yesOrNo(user.canCheckByName))

		val showQr = rememberFalse()
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

		ShowQrDialog(opened = showQr, qrData = token)

		CartedGroupBox(indicatorText = dynamicTranslation("user.detail.mealsIntro")) {
			if (user.meals.isEmpty()) Text(text = DefaultSay.EMPTY, style = typo.bodyMedium)
			else {
				user.meals.forEach { meal ->
					ProfileRow(
						label = dynamicTranslation(mealWindows.first { it.id == meal.windowId }.nameKey),
						value = dynamicTranslation(meal.variantKey),
					)
				}
			}
		}

		if (user.customData.isNotEmpty()) {
			user.customData.forEach { (key, value) ->
				ProfileRow(
					label = key,
					value = value?.toString().orNullIf { it?.isBlank() ?: true } ?: "-",
				)
			}
		}

		DefaultHeightSpacer(2)

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.End,
		) {
			LogoutButton {
				coroutineScope.launch {
					authRepository.logout()
					QoNFerenCeRApp.currentUser.setDetails(null)
				}
			}
		}
	}
}

@Composable
private fun ProfileRow(label: String, value: String, modifier: Modifier = Modifier) {
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Text(text = label, style = typo.bodySmall, textAlign = TextAlign.Start)
		Text(text = value, style = typo.bodyLarge, textAlign = TextAlign.End)
	}
}

/**
 * Logs out and clears session
 * @param onClick Performs logout
 */
@Composable
private fun LogoutButton(onClick: () -> Unit) {
	CardLayout(
		borderize = true,
		innerPads = PADS_NONE,
		containerColor = colors.clickable,
	) {
		Text(
			text = dynamicTranslation("user.detail.logout"),
			style = typo.labelLarge,
			modifier = Modifier
				.clickable(onClick = onClick)
				.defaultTextPadding(),
		)
	}
}

/**
 * Opens login-key QR dialog
 * @param onClick Shows dialog
 */
@Composable
private fun ShowQrButton(onClick: () -> Unit) {
	Icon(
		imageVector = Icons.Default.QrCode2,
		contentDescription = "show QR code",
		tint = colors.text,
		modifier = Modifier
			.defaultClip()
			.background(colors.clickable)
			.clickable(onClick = onClick)
			.defaultTextPadding(2F)
			.size(defaultIconSize * 2F),
	)
}
