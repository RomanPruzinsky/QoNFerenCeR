package tr.qonferencer.screens.aboutapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CartedGroupBox
import tr.qonferencer.trons.defaultLayouts.IconIndicator
import tr.qonferencer.trons.miscs.setClipboardTextWithNotification
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultTextPadding

/** Contact mail for QoNFerenCeR developer */
private const val DEVELOPER_MAIL = "pruzinsky.roman.tr@gmail.com"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.DeveloperContactLayout() {
	CartedGroupBox(
		indicatorText = dynamicTranslation("aboutApp.developerContact"),
		modifier = Modifier.align(Alignment.CenterHorizontally),
		textStyle = typo.labelSmall,
	) {
		val clipboard = LocalClipboard.current
		val context = LocalContext.current
		val scope = rememberCoroutineScope()
		val copiedText = dynamicTranslation("misc.copied")

		IconIndicator {
			Text(
				text = DEVELOPER_MAIL,
				style = typo.bodyMedium,
				modifier = Modifier
					.defaultClip()
					.combinedClickable(
						onClick = {
							setClipboardTextWithNotification(
								clipboardTitle = "QoNFerenCeR Developer Mail",
								textToCopy = DEVELOPER_MAIL,
								textIndicator = copiedText,
								clipboard = clipboard,
								context = context,
								scope = scope,
							)
						},
					)
					.defaultTextPadding(),
			)
		}
	}
}
