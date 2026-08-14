package tr.qonferencer.screens.login

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/** Live camera preview that decodes QR code and reports it via [onDecode] */
@OptIn(ExperimentalGetImage::class)
@Composable
fun QrScannerView(onDecode: (String) -> Unit) {
	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current
	val scanner = remember {
		BarcodeScanning.getClient(BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build())
	}

	val scanExecutorService = remember { Executors.newSingleThreadExecutor() }
	val scanExecutor = remember { Executor { command -> runCatching { scanExecutorService.execute(command) } } }
	var hasDecoded = false

	val cameraController = remember {
		LifecycleCameraController(context).apply {
			setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
			cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
			imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
			setImageAnalysisAnalyzer(scanExecutor) { imageProxy ->
				val mediaImage = imageProxy.image
				if (mediaImage == null || hasDecoded) {
					imageProxy.close()
					return@setImageAnalysisAnalyzer
				}

				val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
				scanner.process(image)
					.addOnSuccessListener(scanExecutor) { barcodes ->
						if (hasDecoded) return@addOnSuccessListener
						val value = barcodes.firstOrNull()?.rawValue ?: return@addOnSuccessListener
						hasDecoded = true
						onDecode(value)
					}
					.addOnCompleteListener(scanExecutor) { imageProxy.close() }
			}
			bindToLifecycle(lifecycleOwner)
		}
	}

	DisposableEffect(Unit) {
		onDispose {
			cameraController.unbind()
			scanner.close()
			scanExecutorService.shutdown()
		}
	}

	AndroidView(
		modifier = Modifier.fillMaxSize(),
		factory = { PreviewView(it).apply { controller = cameraController } },
	)
}

//TODO: test

//TODO: enum for scan "QR/BAR"
