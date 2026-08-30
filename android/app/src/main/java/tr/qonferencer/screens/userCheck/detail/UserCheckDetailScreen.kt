package tr.qonferencer.screens.userCheck.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CartedGroupBox
import tr.qonferencer.trons.defaultLayouts.CustomDropdownMenu
import tr.qonferencer.trons.defaultLayouts.DefaultHeightSpacer
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.DefaultWideDivider
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.DEFAULT_STATE_CHANGE_DELAY_SECS
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.ops.orNullIf
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.remembers.switch
import tr.qonferencer.trons.states.StateIndicator
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.dataState.DataStateLayout
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun UserCheckDetailScreen(user: UserDetailDto) {
	val editVM = viewModel<EditUserDetailsViewModel>(
		key = user.userId.toString(),
		factory = editUserDetailsViewModelFactory(user),
	)
	
	val canEdit = UserDetailDto.roleOrAnonym(QoNFerenCeRApp.currentUser.details.collectValue()).atLeast(Role.ADMIN)
	val editMode = rememberFalse()
	val roleExpanded = rememberFalse()
	val updateResult by editVM.updateState.collectAsState()
	
	val currentData by editVM.detail.collectAsState()
	val fullNameEdit = remember(currentData) { mutableStateOf(currentData.fullName) }
	val roleIndex = remember(currentData) { mutableIntStateOf(currentData.role.ordinal) }
	val isSpeakerEdit = remember(currentData) { mutableStateOf(currentData.isSpeaker) }
	val canCheckUsersEdit = remember(currentData) { mutableStateOf(currentData.canCheckUsers) }
	val canFoodCheckEdit = remember(currentData) { mutableStateOf(currentData.canFoodCheck) }
	
	val mealWindows = QoNFerenCeRApp.mealWindows.windows.collectValue()
	
	DataStateLayout(
		state = updateResult,
		bodyOnWaiting = {
			ScrollableColumn(
				modifier = Modifier
					.fillMaxSize()
					.defaultLayoutPadding(),
				verticalArrangement = defaultSpacing,
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				if (canEdit) {
					EditButtonsRow(
						editing = editMode.value,
						onEditingChange = { editMode.switch() },
						onSave = {
							editVM.save(
								ModifyableUserDataDto(
									fullName = fullNameEdit.value.trim(),
									role = Role.entries[roleIndex.intValue],
									isSpeaker = isSpeakerEdit.value,
									canCheckUsers = canCheckUsersEdit.value,
									canFoodCheck = canFoodCheckEdit.value,
									meals = currentData.meals,
									customData = currentData.customData,
								),
							)
						},
					)
				}
				
				if (editMode.value) {
					DefaultOTF(
						valueText = fullNameEdit,
						labelText = dynamicTranslation("userCheck.detail.fullName"),
						modifier = Modifier.fillMaxWidth(),
						specialCharFilter = { it != '\n' },
					)
				} else {
					Text(
						text = currentData.fullName,
						style = typo.displayLarge,
						modifier = Modifier.fillMaxWidth(),
					)
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
					ProfileToggleRow(dynamicTranslation("user.detail.canCheckUsers"), canCheckUsersEdit)
					ProfileToggleRow(dynamicTranslation("user.detail.canFoodCheck"), canFoodCheckEdit)
				} else {
					ProfileRow(dynamicTranslation("user.detail.role"), currentData.role.name)
					ProfileRow(dynamicTranslation("user.detail.isSpeaker"), DefaultSay.yesOrNo(currentData.isSpeaker))
					ProfileRow(
						dynamicTranslation("user.detail.canCheckUsers"),
						DefaultSay.yesOrNo(currentData.canCheckUsers),
					)
					ProfileRow(
						dynamicTranslation("user.detail.canFoodCheck"),
						DefaultSay.yesOrNo(currentData.canFoodCheck),
					)
				}
				
				ProfileRow(dynamicTranslation("user.detail.userId"), currentData.userId.toString())

				if (currentData.meals.isEmpty()) DefaultWideDivider()
				else {
					CartedGroupBox(indicatorText = dynamicTranslation("user.detail.mealsIntro")) {
						currentData.meals.forEach { meal ->
							ProfileRow(
								label = dynamicTranslation(mealWindows.first { it.id == meal.windowId }.nameKey),
								value = dynamicTranslation(meal.variantKey),
							)
						}
					}
				}
				
				if (currentData.customData.isNotEmpty()) {
					currentData.customData.forEach { (key, value) ->
						ProfileRow(
							label = key,
							value = value?.toString().orNullIf { it?.isBlank() ?: true } ?: "-",
						)
					}
				}
			}
		},
		bodyOnSuccess = { updateData ->
			StateIndicator(text = DefaultSay.SUCCESS)
			LaunchedEffect(updateData) {
				editMode.value = false
				delay(DEFAULT_STATE_CHANGE_DELAY_SECS)
				editVM.reset()
			}
		},
	)
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

@Composable
fun EditButtonsRow(
	editing: Boolean,
	onEditingChange: () -> Unit,
	onSave: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		@Composable
		fun EditIcon(
			icon: ImageVector,
			background: Color,
			contentDesctiption: String,
			onClick: () -> Unit,
		) {
			Icon(
				imageVector = icon,
				contentDescription = contentDesctiption,
				tint = colors.text,
				modifier = Modifier
					.defaultClip()
					.background(background)
					.clickable(onClick = onClick)
					.defaultTextPadding()
					.size(defaultIconSize),
			)
		}
		EditIcon(
			icon = if (editing) Icons.Default.Visibility else Icons.Default.Edit,
			background = colors.clickable,
			contentDesctiption = "switch edit mode",
			onClick = onEditingChange,
		)
		
		if (editing) {
			EditIcon(
				icon = Icons.Default.Check,
				background = colors.action.approve,
				contentDesctiption = "approve",
				onClick = onSave,
			)
		}
	}
}
