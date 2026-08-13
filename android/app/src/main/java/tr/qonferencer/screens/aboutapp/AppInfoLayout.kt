package tr.qonferencer.screens.aboutapp

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.oss.licenses.v2.OssLicensesMenuActivity
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.PADS_DEFAULT_HALF
import tr.qonferencer.trons.defaultLayouts.PADS_NONE
import tr.qonferencer.trons.miscs.getVersionCode
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun AppInfoLayout() {
	val context = LocalContext.current
	val appVersion = getVersionCode(context)

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.defaultLayoutPadding(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		AppInfoButton(
			text = dynamicTranslation("aboutApp.licence"),
			action = { context.startActivity(Intent(context, OssLicensesMenuActivity::class.java)) },
		)

		AppInfoButton(
			text = dynamicTranslation("aboutApp.appVersion") + ": " + appVersion.toString(),
		)
	}
}

@Composable
private fun AppInfoButton(text: String, action: (() -> Unit)? = null) {
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
