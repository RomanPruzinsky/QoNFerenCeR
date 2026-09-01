package tr.qonferencer.screens.admin.createSlot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.screens.admin.CustomDataEditor
import tr.qonferencer.screens.admin.CustomDataFieldState
import tr.qonferencer.screens.admin.MealEditor
import tr.qonferencer.screens.admin.UserLoginCredentialsScreen
import tr.qonferencer.screens.admin.mealVariantKeys
import tr.qonferencer.screens.admin.toCustomData
import tr.qonferencer.screens.admin.toMealFieldStates
import tr.qonferencer.screens.admin.toMeals
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.theme.colors
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.ApproveIconButton
import tr.qonferencer.trons.defaultLayouts.CartedGroupBox
import tr.qonferencer.trons.defaultLayouts.CustomDropdownMenu
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.DefaultWideDivider
import tr.qonferencer.trons.defaultLayouts.ProfileEditRow
import tr.qonferencer.trons.defaultLayouts.ProfileToggleRow
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.cannotBeEmptyToast
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.remembers.rememberEmptyString
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.dataState.DataStateLayout
import tr.qonferencer.trons.theme.defaultLayoutPadding

@Composable
fun CreateSlotScreen() {
	val createVM = viewModel<CreateSlotViewModel>()
	
	DataStateLayout(
		stateFlow = createVM.createState,
		bodyOnWaiting = { CreateSlotForm(onSubmit = createVM::create) },
		bodyOnSuccess = { provisioned ->
			UserLoginCredentialsScreen(
				fullName = provisioned.user.fullName,
				credentials = provisioned.credentials,
				doneLabel = dynamicTranslation("admin.createSlot.createAnother"),
				onDone = createVM::reset,
			)
		},
	)
}

@Composable
private fun CreateSlotForm(onSubmit: (ModifyableUserDataDto) -> Unit) {
	val context = LocalContext.current
	
	val fullName = rememberEmptyString()
	val roleIndex = remember { mutableIntStateOf(Role.VISITOR.ordinal) }
	val roleExpanded = rememberFalse()
	val isSpeaker = rememberFalse()
	val canCheckUsers = rememberFalse()
	val canFoodCheck = rememberFalse()
	val customFields = remember { mutableStateListOf<CustomDataFieldState>() }
	val mealWindows = QoNFerenCeRApp.mealWindows.windows.collectValue()
	val variantKeys = QoNFerenCeRApp.language.options.collectValue().translations.mealVariantKeys()
	val mealFields = remember(mealWindows, variantKeys) { mealWindows.toMealFieldStates(emptyList(), variantKeys) }
	
	ScrollableColumn(
		modifier = Modifier
			.fillMaxSize()
			.defaultLayoutPadding(),
		verticalArrangement = defaultSpacing,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		DefaultOTF(
			valueText = fullName,
			labelText = dynamicTranslation("userCheck.detail.fullName"),
			modifier = Modifier.fillMaxWidth(),
			specialCharFilter = { it != '\n' },
		)
		
		ProfileEditRow(dynamicTranslation("user.detail.role")) {
			CustomDropdownMenu(
				options = Role.entries.relist { it.name },
				selected = roleIndex,
				expanded = roleExpanded,
				selectedColor = colors.clickable,
			)
		}
		ProfileToggleRow(dynamicTranslation("user.detail.isSpeaker"), isSpeaker)
		ProfileToggleRow(dynamicTranslation("user.detail.canCheckUsers"), canCheckUsers)
		ProfileToggleRow(dynamicTranslation("user.detail.canFoodCheck"), canFoodCheck)
		
		if (mealWindows.isNotEmpty()) {
			CartedGroupBox(indicatorText = dynamicTranslation("user.detail.mealsIntro")) {
				MealEditor(mealFields)
			}
		}
		
		DefaultWideDivider()
		CustomDataEditor(fields = customFields)

		ApproveIconButton(
			contentDescription = "create slot",
			modifier = Modifier.align(Alignment.End),
			onClick = {
				if (fullName.value.isBlank()) {
					cannotBeEmptyToast(context)
					return@ApproveIconButton
				}
				onSubmit(
					ModifyableUserDataDto(
						fullName = fullName.value.trim(),
						role = Role.fromIndex(roleIndex.intValue),
						isSpeaker = isSpeaker.value,
						canCheckUsers = canCheckUsers.value,
						canFoodCheck = canFoodCheck.value,
						meals = mealFields.toMeals(),
						customData = customFields.toCustomData(),
					),
				)
			},
		)
	}
}
