package tr.qonferencer.screens.mealScan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.shared.dtos.MealCountDto
import tr.qonferencer.shared.dtos.MealScanRequestDto
import tr.qonferencer.shared.dtos.MealScanResultDto
import tr.qonferencer.shared.dtos.MealWindowDto
import tr.qonferencer.shared.enums.MealScanResult
import tr.qonferencer.shared.enums.ScannerType
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState
import tr.qonferencer.trons.states.dataState.waiting
import java.time.Instant
import java.util.UUID

class MealScanViewModel : ViewModel() {
	private val audio = MealScanAudioManager(QoNFerenCeRApp.appContext, QoNFerenCeRApp.mealScanAudioPrefs.soundEnabled)
	
	private val _currentWindow = MutableStateFlow(currentMealWindow())
	val currentWindow = _currentWindow.asStateFlow()
	
	private val _scanState = initDataState<MealScanResultDto>()
	val scanState = _scanState.asStateFlow()
	
	private val _countsState = initDataState<List<MealCountDto>>()
	val countsState = _countsState.asStateFlow()
	
	private var lastToken: String? = null
	
	init {
		loadCounts()
	}

	/** Picks nearest window */
	fun pickCurrentWindow() {
		pickWindow(currentMealWindow() ?: return)
	}
	
	fun pickWindow(window: MealWindowDto) {
		_currentWindow.value = window
		resetToWaiting()
		loadCounts()
	}
	
	fun scan(
		token: String,
		scannerType: ScannerType,
	) {
		val window = _currentWindow.value ?: return
		if (_scanState.value == DataState.Processing) return
		if (token == lastToken && _scanState.value is DataState.Success) return
		lastToken = token
		
		dataStatedAction(_scanState) {
			val result = QoNFerenCerApi.meal.scan(MealScanRequestDto(token, window.id, UUID.randomUUID(), scannerType))
			loadCounts()
			result
		}
	}
	
	fun scanManual(userId: Long) = scan(userId.toString(), ScannerType.MANUAL)
	
	fun playAudio(result: MealScanResult) {
		if (result == MealScanResult.APPROVED) audio.approved()
		else audio.denied()
	}
	
	fun resetToWaiting() {
		lastToken = null
		_scanState.waiting()
	}
	
	private fun loadCounts() {
		val window = _currentWindow.value ?: return
		dataStatedAction(_countsState) { QoNFerenCerApi.meal.counts(window.id) }
	}
	
	override fun onCleared() {
		audio.release()
	}
}

/** @return Window open right now or nearest upcoming one, or `null` if none exist */
private fun currentMealWindow(): MealWindowDto? {
	val windows = QoNFerenCeRApp.mealWindows.windows.value
	val now = Instant.now()
	return windows.firstOrNull { now in it.startsAt..it.endsAt }
		?: windows.firstOrNull { it.startsAt > now }
		?: windows.lastOrNull()
}
