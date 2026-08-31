package tr.qonferencer.trons.defaultLayouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.remembers.switch

/** Displays info about user */
@Composable
fun ProfileDisplayRow(
	label: String,
	value: String,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Text(text = label, style = typo.bodySmall, textAlign = TextAlign.Start)
		Text(text = value, style = typo.bodyLarge, textAlign = TextAlign.End)
	}
}

/** Editable [ProfileDisplayRow] */
@Composable
fun ProfileEditRow(
	label: String,
	content: @Composable () -> Unit,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(text = label, style = typo.bodySmall, textAlign = TextAlign.Start)
		content()
	}
}

/** Togglable [ProfileDisplayRow] */
@Composable
fun ProfileToggleRow(
	label: String,
	value: MutableState<Boolean>,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Text(text = label, style = typo.bodySmall, textAlign = TextAlign.Start)
		Text(
			text = DefaultSay.yesOrNo(value.value),
			style = typo.bodyLarge,
			modifier = Modifier.clickable { value.switch() },
		)
	}
}
