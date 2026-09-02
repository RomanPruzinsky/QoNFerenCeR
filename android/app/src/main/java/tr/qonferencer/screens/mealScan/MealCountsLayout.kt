package tr.qonferencer.screens.mealScan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.flow.StateFlow
import tr.qonferencer.shared.dtos.MealCountDto
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CartedGroupBox
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.dataState.DataStateLayout

@Composable
fun MealCountsLayout(countsState: StateFlow<DataState<List<MealCountDto>>>) {
	CartedGroupBox(indicatorText = dynamicTranslation("mealScan.counts.intro")) {
		DataStateLayout(stateFlow = countsState) { counts ->
			if (counts.isEmpty()) Text(text = DefaultSay.EMPTY, style = typo.bodyMedium)
			else counts.forEach { count ->
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(text = dynamicTranslation(count.variantKey), style = typo.bodySmall, textAlign = TextAlign.Start)
					Text(text = count.remaining.toString(), style = typo.bodyLarge, textAlign = TextAlign.End)
				}
			}
		}
	}
}
