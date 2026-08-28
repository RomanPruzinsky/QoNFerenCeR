package tr.qonferencer.screens.userCheck.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CardLayout
import tr.qonferencer.trons.defaultLayouts.CartedGroupBox
import tr.qonferencer.trons.defaultLayouts.CustomDropdownMenu
import tr.qonferencer.trons.defaultLayouts.DefaultHeightSpacer
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.PADS_NONE
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.ops.orNullIf
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.remembers.switch
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.dataState.OnError
import tr.qonferencer.trons.states.dataState.OnProcessing
import tr.qonferencer.trons.states.errorIndicatorMessage
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun UserCheckDetailScreen(user: UserDetailDto) {
	val editVM: EditUserDetailsViewModel = viewModel(
		key = user.userId.toString(),
		factory = editUserDetailsViewModelFactory(user),
	)
	
	val canEdit = UserDetailDto.roleOrAnonym(QoNFerenCeRApp.currentUser.details.collectValue()).atLeast(Role.ADMIN)
	val editMode = rememberFalse()
	val mealWindows = QoNFerenCeRApp.mealWindows.windows.collectValue()
	
	val current by editVM.detail.collectAsState()
	
	val fullNameEdit = remember(current) { mutableStateOf(current.fullName) }
	val roleIndex = remember(current) { mutableIntStateOf(current.role.ordinal) }
	val roleExpanded = rememberFalse()
	val isSpeakerEdit = remember(current) { mutableStateOf(current.isSpeaker) }
	val canCheckByNameEdit = remember(current) { mutableStateOf(current.canCheckByName) }
	
	val updateResult by editVM.updateState.collectAsState()
	LaunchedEffect(updateResult) {
		if (updateResult is DataState.Success) editMode.value = false
	}
	
	ScrollableColumn(
		modifier = Modifier
			.fillMaxWidth()
			.defaultLayoutPadding(),
		verticalArrangement = defaultSpacing,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		if (canEdit) {
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
				EditSwitchButton(editing = editMode.value) { editMode.switch() }
			}
		}
		
		if (editMode.value) {
			DefaultOTF(
				valueText = fullNameEdit,
				labelText = dynamicTranslation("userCheck.detail.fullName"),
				modifier = Modifier.fillMaxWidth(),
			)
		} else {
			Text(text = current.fullName, style = typo.displayLarge)
		}
		
		DefaultHeightSpacer(2)
		
		if (editMode.value) {
			ProfileEditRow(dynamicTranslation("user.detail.role")) {
				CustomDropdownMenu(
					options = Role.entries.relist { it.name },
					selected = roleIndex,
					expanded = roleExpanded,
					arrowAtStart = false,
					selectedColor = colors.clickable,
				)
			}
			ProfileToggleRow(dynamicTranslation("user.detail.isSpeaker"), isSpeakerEdit)
			ProfileToggleRow(dynamicTranslation("user.detail.canCheckByName"), canCheckByNameEdit)
		} else {
			ProfileRow(dynamicTranslation("user.detail.role"), current.role.name)
			ProfileRow(dynamicTranslation("user.detail.isSpeaker"), DefaultSay.yesOrNo(current.isSpeaker))
			ProfileRow(dynamicTranslation("user.detail.canCheckByName"), DefaultSay.yesOrNo(current.canCheckByName))
		}
		
		ProfileRow(dynamicTranslation("user.detail.userId"), current.userId.toString())
		
		CartedGroupBox(indicatorText = dynamicTranslation("user.detail.mealsIntro")) {
			if (current.meals.isEmpty()) Text(text = DefaultSay.EMPTY, style = typo.bodyMedium)
			else {
				current.meals.forEach { meal ->
					ProfileRow(
						label = dynamicTranslation(mealWindows.first { it.id == meal.windowId }.nameKey),
						value = dynamicTranslation(meal.variantKey),
					)
				}
			}
		}
		
		if (current.customData.isNotEmpty()) {
			current.customData.forEach { (key, value) ->
				ProfileRow(
					label = key,
					value = value?.toString().orNullIf { it?.isBlank() ?: true } ?: "-",
				)
			}
		}
		
		if (editMode.value) {
			DefaultHeightSpacer(2)
			
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
				SaveButton {
					editVM.save(
						ModifyableUserDataDto(
							fullName = fullNameEdit.value,
							role = Role.entries[roleIndex.intValue],
							isSpeaker = isSpeakerEdit.value,
							canCheckByName = canCheckByNameEdit.value,
							meals = current.meals,
							customData = current.customData,
						),
					)
				}
			}
			
			editVM.updateState.OnProcessing { CircularProgressIndicator() }
			editVM.updateState.OnError { e ->
				Text(
					text = errorIndicatorMessage(e),
					style = typo.bodyMedium,
					color = colors.action.delete,
				)
			}
		}
	}
}

@Composable
private fun ProfileRow(
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

@Composable
private fun ProfileEditRow(
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

@Composable
private fun ProfileToggleRow(
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

/**
 * Switches between viewing and editing
 * @param onClick Toggles edit mode
 */
@Composable
private fun EditSwitchButton(
	editing: Boolean,
	onClick: () -> Unit,
) {
	Icon(
		imageVector = if (editing) Icons.Default.Visibility else Icons.Default.Edit,
		contentDescription = "switch edit mode",
		tint = colors.text,
		modifier = Modifier
			.defaultClip()
			.background(colors.clickable)
			.clickable(onClick = onClick)
			.defaultTextPadding(2F)
			.size(defaultIconSize),
	)
}

/**
 * Saves current edits
 * @param onClick Submits edited data
 */
@Composable
private fun SaveButton(onClick: () -> Unit) {
	CardLayout(
		borderize = true,
		innerPads = PADS_NONE,
		containerColor = colors.clickable,
	) {
		Text(
			text = dynamicTranslation("userCheck.detail.save"),
			style = typo.labelLarge,
			modifier = Modifier
				.clickable(onClick = onClick)
				.defaultTextPadding(),
		)
	}
}
