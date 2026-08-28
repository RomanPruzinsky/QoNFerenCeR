package tr.qonferencer.screens.aboutApp

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.google.android.gms.oss.licenses.v2.OssLicensesMenuActivity
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.PADS_DEFAULT_HALF
import tr.qonferencer.trons.defaultLayouts.PADS_NONE
import tr.qonferencer.trons.miscs.getVersionCode
import tr.qonferencer.trons.theme.defaultTextPadding

private const val QONFERENCER_GITHUB_URL = "https://github.com/RomanPruzinsky/QoNFerenCeR"

@Composable
fun ColumnScope.AppInfoLayout() {
	val context = LocalContext.current
	val appVersion = getVersionCode(context)

	AppInfoButton(
		text = dynamicTranslation("aboutApp.github"),
		action = { context.startActivity(Intent(Intent.ACTION_VIEW, QONFERENCER_GITHUB_URL.toUri())) },
	)

	AppInfoButton(
		text = dynamicTranslation("aboutApp.licence"),
		action = { context.startActivity(Intent(context, OssLicensesMenuActivity::class.java)) },
	)

	AppInfoButton(
		text = dynamicTranslation("aboutApp.appVersion") + ": " + appVersion.toString(),
	)
}

@Composable
private fun AppInfoButton(
	text: String,
	action: (() -> Unit)? = null,
) {
	CardLayout(
		outerPads = PADS_DEFAULT_HALF,
		innerPads = PADS_NONE,
	) {
		Text(
			text = text,
			style = typo.bodyMedium,
			modifier = Modifier
				.clickable(enabled = action != null) { action!!() }
				.defaultTextPadding(),
		)
	}
}
