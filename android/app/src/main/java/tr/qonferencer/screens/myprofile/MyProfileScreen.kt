package tr.qonferencer.screens.myprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.theme.color
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CartedGroupBox
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.theme.defaultLayoutPadding

@Composable
fun MyProfileScreen(modifier: Modifier = Modifier) {
	val user = QoNFerenCeRApp.currentUser.details.collectValue()
	//TODO: not programmed by me

	ScrollableColumn(
		modifier = modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
		verticalArrangement = defaultSpacing,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		if (user == null) {
			Text(text = dynamicTranslation("myProfile.noData"), style = typo.bodyMedium)
			return@ScrollableColumn
		}

		Text(text = user.fullName, style = typo.headlineMedium)
		Text(text = user.role.name.lowercase(), style = typo.titleMedium, color = user.role.color)

		CartedGroupBox(indicatorText = dynamicTranslation("myProfile.details")) {
			ProfileFieldRow(dynamicTranslation("myProfile.userId"), user.userId.toString())
			ProfileFieldRow(dynamicTranslation("myProfile.role"), user.role.name.lowercase())
			ProfileFieldRow(dynamicTranslation("myProfile.isSpeaker"), yesNo(user.isSpeaker))
			ProfileFieldRow(dynamicTranslation("myProfile.canCheckByName"), yesNo(user.canCheckByName))
		}

		CartedGroupBox(indicatorText = dynamicTranslation("myProfile.meals")) {
			if (user.meals.isEmpty()) {
				Text(text = dynamicTranslation("myProfile.noMeals"), style = typo.bodyMedium)
			} else {
				user.meals.forEach { meal ->
					ProfileFieldRow(
						"${dynamicTranslation("myProfile.mealWindow")} #${meal.windowId}",
						dynamicTranslation(meal.variantKey),
					)
				}
			}
		}

		if (user.customData.isNotEmpty()) {
			CartedGroupBox(indicatorText = dynamicTranslation("myProfile.customData")) {
				user.customData.forEach { (key, value) -> ProfileFieldRow(key, value?.toString() ?: "-") }
			}
		}
	}
}

@Composable
private fun yesNo(value: Boolean) = dynamicTranslation(if (value) "myProfile.yes" else "myProfile.no")

@Composable
private fun ProfileFieldRow(label: String, value: String, modifier: Modifier = Modifier) {
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Text(text = label, style = typo.titleSmall)
		Text(text = value, style = typo.bodyMedium, textAlign = TextAlign.End)
	}
}
