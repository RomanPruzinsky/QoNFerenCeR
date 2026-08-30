package tr.qonferencer.screens.mealScan

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.flow.StateFlow
import tr.qonferencer.R

class MealScanAudioManager(
	private val context: Context,
	private val soundEnabled: StateFlow<Boolean>,
) {
	private var player: MediaPlayer? = null

	/** Plays random "mňam mňam" sound for approved scan */
	fun approved() = playRandom(APPROVED_SOUNDS)

	/** Plays random "ne ne" sound for rejected scan */
	fun denied() = playRandom(DENIED_SOUNDS)

	/** Releases underlying [MediaPlayer] */
	fun release() {
		player?.release()
		player = null
	}
	
	private fun playRandom(sounds: List<Int>) {
		if (!soundEnabled.value) return
		player?.release()
		player = MediaPlayer.create(context, sounds.random())
		player?.start()
	}
	
	private companion object {
		val APPROVED_SOUNDS = listOf(R.raw.mnammnam_uno, R.raw.mnammnam_due, R.raw.mnammnam_tre, R.raw.mnammnam_quattro)
		val DENIED_SOUNDS = listOf(R.raw.ee_uno, R.raw.ee_due, R.raw.ee_tre, R.raw.ee_quattro)
	}
}
