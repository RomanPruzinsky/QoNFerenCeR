package tr.qonferencer.trons.states

import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.infoState.InfoState

/** Default [String] values for [InfoState] or [DataState] */
object StateSay {
	const val WAITING: String = "✨🌟🌙"
	const val PROCESSING: String = "🔨🔧⛏️"
	const val SUCCESS: String = "✅✅✅"
	const val ERROR: String = "🚨❌💥"
	const val OFFLINE: String = "📡💥🛜"
}
