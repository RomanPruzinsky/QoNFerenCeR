package tr.qonferencer.trons.miscs

import androidx.compose.runtime.Composable
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.infoState.InfoState

/** Default [String] values for [InfoState] or [DataState] or similar */
object DefaultSay {
	const val WAITING = "✨🌟🌙"
	const val PROCESSING = "🔨🔧⛏️"
	const val SUCCESS = "✅✅✅"
	const val ERROR = "🚨❌💥"

	const val OFFLINE = "📡💥🛜"
	const val REFRESH = "⟳\uD83D\uDD01⟲"

	const val YES = "✅"
	const val NO = "❌"

	const val EMPTY = "\uD83D\uDC40\uD83D\uDD0D❌"

	@Composable
	fun yesOrNo(value: () -> Boolean) = yesOrNo(value())

	@Composable
	fun yesOrNo(value: Boolean) = dynamicTranslation(if (value) YES else NO)
}
