package tr.qonferencer.screens.login

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

/**
 * Live camera preview that decodes the first QR code seen and reports it once via [onDecode]
 * @param onDecode Called with the raw QR content, exactly once per composition
 */
@Composable
fun QrScannerView(onDecode: (String) -> Unit, modifier: Modifier = Modifier) {
	val lifecycleOwner = LocalLifecycleOwner.current
	val scanner = remember {
		BarcodeScanning.getClient(BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build())
	}
	// Every frame is analyzed and every ML Kit callback confined to this one thread, so `hasDecoded` needs no locking.
	val scanExecutor = remember { Executors.newSingleThreadExecutor() }
	var hasDecoded = false
	val cameraProviderHolder = remember { arrayOfNulls<ProcessCameraProvider>(1) }

	DisposableEffect(Unit) {
		onDispose {
			cameraProviderHolder[0]?.unbindAll()
			scanner.close()
			scanExecutor.shutdown()
		}
	}

	AndroidView(
		modifier = modifier,
		factory = { ctx ->
			val previewView = PreviewView(ctx)
			val mainExecutor = ContextCompat.getMainExecutor(ctx)
			val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

			cameraProviderFuture.addListener({
				val provider = cameraProviderFuture.get()
				cameraProviderHolder[0] = provider

				val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
				val analysis = ImageAnalysis.Builder()
					.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
					.build()

				analysis.setAnalyzer(scanExecutor) { imageProxy ->
					val mediaImage = imageProxy.image
					if (mediaImage == null || hasDecoded) {
						imageProxy.close()
						return@setAnalyzer
					}

					val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
					scanner.process(image)
						.addOnSuccessListener(scanExecutor) { barcodes ->
							val value = barcodes.firstNotNullOfOrNull { it.rawValue }
							if (value != null && !hasDecoded) {
								hasDecoded = true
								onDecode(value)
							}
						}
						.addOnCompleteListener(scanExecutor) { imageProxy.close() }
				}

				provider.unbindAll()
				provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
			}, mainExecutor)

			previewView
		},
	)
}
