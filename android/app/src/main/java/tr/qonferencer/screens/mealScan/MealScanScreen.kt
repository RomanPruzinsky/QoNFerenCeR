package tr.qonferencer.screens.mealScan

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.barcode.common.Barcode
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.nfc.ScanNfc
import tr.qonferencer.screens.keyInputMethod.KeyInputMethod
import tr.qonferencer.screens.login.QrScannerView
import tr.qonferencer.shared.dtos.MealScanResultDto
import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.ScannerType
import tr.qonferencer.theme.color
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.dataState.DataStateLayout
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun MealScanScreen() {
	val mealScanVM = viewModel<MealScanViewModel>()
	val currentWindow = mealScanVM.currentWindow.collectValue()
	
	if (currentWindow == null) {
		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = dynamicTranslation("mealScan.noWindow"),
				style = typo.headlineMedium,
			)
		}
		return
	}
	
	val hasCameraPermission = rememberHasCameraPermission()
	val nfcAvailable = rememberIsNfcAvailable()
	
	var selectedMethod by remember { mutableStateOf(KeyInputMethod.NFC) }
	var isQrScannerOpen by rememberFalse()
	
	if (isQrScannerOpen) {
		BackHandler { isQrScannerOpen = false }
		QrScannerView(
			format = Barcode.FORMAT_ALL_FORMATS,
			onDecode = { token, format ->
				isQrScannerOpen = false
				val scannerType =
					if (format == Barcode.FORMAT_QR_CODE) ScannerType.QR
					else ScannerType.BARCODE
				mealScanVM.scan(token, scannerType)
			},
		)
		return
	}
	
	Column(
		modifier = Modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
		verticalArrangement = defaultSpacing,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		MealWindowPicker(
			currentWindow = currentWindow,
			windows = QoNFerenCeRApp.mealWindows.windows.collectValue(),
			onPickCurrent = mealScanVM::pickCurrentWindow,
			onPickWindow = mealScanVM::pickWindow,
		)
		
		Box(
			modifier = Modifier
				.weight(1F)
				.fillMaxWidth(),
			contentAlignment = Alignment.Center,
		) {
			DataStateLayout(stateFlow = mealScanVM.scanState) { result ->
				LaunchedEffect(result) { mealScanVM.playAudio(result.result) }
				MealScanResultText(result)
			}
		}
		
		MealScanMethodAction(
			method = selectedMethod,
			nfcAvailable = nfcAvailable,
			hasCameraPermission = hasCameraPermission,
			onScanNfc = { mealScanVM.scan(it, ScannerType.NFC) },
			onOpenQrScanner = { isQrScannerOpen = true },
			onManualSubmit = mealScanVM::scanManual,
		)
		
		MealScanMethodPicker(
			selectedMethod = selectedMethod,
			onMethodSelect = { method ->
				if (method == KeyInputMethod.QR_BAR && selectedMethod != KeyInputMethod.QR_BAR && hasCameraPermission) {
					isQrScannerOpen = true
				}
				selectedMethod = method
				mealScanVM.resetToWaiting()
			},
		)
		
		MealCountsLayout(mealScanVM.countsState)
	}
}

@Composable
private fun MealScanMethodAction(
	method: KeyInputMethod,
	nfcAvailable: Boolean,
	hasCameraPermission: Boolean,
	onScanNfc: (String) -> Unit,
	onOpenQrScanner: () -> Unit,
	onManualSubmit: (Long) -> Unit,
) {
	when (method) {
		KeyInputMethod.NFC ->
			if (nfcAvailable) {
				ScanNfc(onDecode = onScanNfc)
				Text(text = dynamicTranslation("mealScan.nfc.scanning"), style = typo.bodySmall)
			} else {
				Text(text = dynamicTranslation("mealScan.nfc.unavailable"), style = typo.bodyMedium)
			}
		
		KeyInputMethod.QR_BAR ->
			if (hasCameraPermission) {
				Icon(
					imageVector = Icons.Default.Refresh,
					contentDescription = "scan QR",
					tint = colors.text,
					modifier = Modifier
						.defaultClip()
						.background(colors.clickable)
						.clickable(onClick = onOpenQrScanner)
						.defaultTextPadding(2F)
						.size(defaultIconSizeLarge),
				)
			} else {
				Text(text = dynamicTranslation("login.state.cameraDenied"), style = typo.bodyMedium)
			}
		
		KeyInputMethod.MANUAL -> ManualMealIdEntry(onSubmit = onManualSubmit)
	}
}

@Composable
private fun MealScanResultText(result: MealScanResultDto) {
	Text(
		text = when (result.result) {
			MealScanResult.APPROVED -> result.variantKey?.let { dynamicTranslation(it) } ?: DefaultSay.SUCCESS
			MealScanResult.ALREADY_CONSUMED -> dynamicTranslation("mealScan.result.alreadyConsumed")
			MealScanResult.NO_USER_FOUND -> dynamicTranslation("mealScan.result.noUserFound")
			MealScanResult.NOT_REGISTERED_PORTION -> dynamicTranslation("mealScan.result.notRegisteredPortion")
		},
		style = typo.displayLarge,
		modifier = Modifier
			.fillMaxWidth()
			.defaultClip()
			.background(result.result.color)
			.defaultTextPadding(),
	)
}
