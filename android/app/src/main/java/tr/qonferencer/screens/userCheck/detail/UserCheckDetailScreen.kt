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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.screens.admin.CustomDataDisplay
import tr.qonferencer.screens.admin.CustomDataEditor
import tr.qonferencer.screens.admin.MealEditor
import tr.qonferencer.screens.admin.UserLoginCredentialsScreen
import tr.qonferencer.screens.admin.mealVariantKeys
import tr.qonferencer.screens.admin.toCustomData
import tr.qonferencer.screens.admin.toFieldStates
import tr.qonferencer.screens.admin.toMealFieldStates
import tr.qonferencer.screens.admin.toMeals
import tr.qonferencer.shared.dtos.ModifyableUserDataDto
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CartedGroupBox
import tr.qonferencer.trons.defaultLayouts.ConfirmDialog
import tr.qonferencer.trons.defaultLayouts.CustomDropdownMenu
import tr.qonferencer.trons.defaultLayouts.DefaultHeightSpacer
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.DefaultWideDivider
import tr.qonferencer.trons.defaultLayouts.ProfileDisplayRow
import tr.qonferencer.trons.defaultLayouts.ProfileEditRow
import tr.qonferencer.trons.defaultLayouts.ProfileToggleRow
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.DEFAULT_STATE_CHANGE_DELAY_SECS
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.miscs.ShortToast
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.remembers.switch
import tr.qonferencer.trons.states.StateIndicator
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.dataState.DataStateLayout
import tr.qonferencer.trons.states.errorIndicatorMessage
import tr.qonferencer.trons.states.infoState.OnError
import tr.qonferencer.trons.states.infoState.OnSuccess
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

/**
 * @param user Slot to show/edit
 * @param onDismiss Called once slot got deleted to leave this screen
 */
@Composable
fun UserCheckDetailScreen(
	user: UserDetailDto,
	onDismiss: () -> Unit,
) {
	val editVM = viewModel<EditUserDetailsViewModel>(
		key = user.userId.toString(),
		factory = editUserDetailsViewModelFactory(user),
	)
	
	val canEdit = UserDetailDto.roleOrAnonym(QoNFerenCeRApp.currentUser.details.collectValue()).atLeast(Role.ADMIN)
	val editMode = rememberFalse()
	val roleExpanded = rememberFalse()
	val revokeConfirmOpen = rememberFalse()
	val deleteConfirmOpen = rememberFalse()
	val reissueConfirmOpen = rememberFalse()
	val updateResult by editVM.updateState.collectAsState()
	val loginCredentials by editVM.loginCredentialsState.collectAsState()
	
	val currentData by editVM.detail.collectAsState()
	val fullNameEdit = remember(currentData) { mutableStateOf(currentData.fullName) }
	val roleIndex = remember(currentData) { mutableIntStateOf(currentData.role.ordinal) }
	val isSpeakerEdit = remember(currentData) { mutableStateOf(currentData.isSpeaker) }
	val canCheckUsersEdit = remember(currentData) { mutableStateOf(currentData.canCheckUsers) }
	val canFoodCheckEdit = remember(currentData) { mutableStateOf(currentData.canFoodCheck) }
	val customDataEdit = remember(currentData) { currentData.customData.toFieldStates().toMutableStateList() }
	
	val mealWindows = QoNFerenCeRApp.mealWindows.windows.collectValue()
	val variantKeys = QoNFerenCeRApp.language.options.collectValue().translations.mealVariantKeys()
	val mealFieldsEdit = remember(currentData, mealWindows, variantKeys) {
		mealWindows.toMealFieldStates(currentData.meals, variantKeys)
	}
	
	ConfirmDialog(
		opened = revokeConfirmOpen,
		message = dynamicTranslation("admin.slot.revoke.confirm"),
		onConfirm = editVM::revoke,
	)
	ConfirmDialog(
		opened = deleteConfirmOpen,
		message = dynamicTranslation("admin.slot.delete.confirm"),
		onConfirm = editVM::delete,
	)
	ConfirmDialog(
		opened = reissueConfirmOpen,
		message = dynamicTranslation("admin.slot.reissue.confirm"),
		onConfirm = editVM::reissueCredentials,
	)
	val onDismissUpdated = rememberUpdatedState(onDismiss)
	
	editVM.revokeState.OnSuccess { ShortToast(text = dynamicTranslation("admin.slot.revoke.success")) }
	editVM.revokeState.OnError { e -> ShortToast(text = errorIndicatorMessage(e)) }
	
	editVM.deleteState.OnSuccess { LaunchedEffect(Unit) { onDismissUpdated.value() } }
	editVM.deleteState.OnError { e -> ShortToast(text = errorIndicatorMessage(e)) }
	
	when (val credentialsState = loginCredentials) {
		is DataState.Success -> {
			UserLoginCredentialsScreen(
				fullName = currentData.fullName,
				credentials = credentialsState.value,
				doneLabel = dynamicTranslation("misc.done"),
				onDone = editVM::resetLoginCredentials,
			)
			return
		}
		
		is DataState.Error -> ShortToast(text = errorIndicatorMessage(credentialsState.specification))
		else -> Unit
	}
	
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
									role = Role.fromIndex(roleIndex.intValue),
									isSpeaker = isSpeakerEdit.value,
									canCheckUsers = canCheckUsersEdit.value,
									canFoodCheck = canFoodCheckEdit.value,
									meals = mealFieldsEdit.toMeals(),
									customData = customDataEdit.toCustomData(),
								),
							)
						},
						onRevoke = { revokeConfirmOpen.value = true },
						onDelete = { deleteConfirmOpen.value = true },
						onReissueCredentials = { reissueConfirmOpen.value = true },
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
					ProfileDisplayRow(dynamicTranslation("user.detail.role"), currentData.role.name)
					ProfileDisplayRow(dynamicTranslation("user.detail.isSpeaker"), DefaultSay.yesOrNo(currentData.isSpeaker))
					ProfileDisplayRow(
						dynamicTranslation("user.detail.canCheckUsers"),
						DefaultSay.yesOrNo(currentData.canCheckUsers),
					)
					ProfileDisplayRow(
						dynamicTranslation("user.detail.canFoodCheck"),
						DefaultSay.yesOrNo(currentData.canFoodCheck),
					)
				}
				
				ProfileDisplayRow(dynamicTranslation("user.detail.userId"), currentData.userId.toString())
				
				if (editMode.value) {
					if (mealWindows.isEmpty()) DefaultWideDivider()
					else {
						CartedGroupBox(indicatorText = dynamicTranslation("user.detail.mealsIntro")) {
							MealEditor(mealFieldsEdit)
						}
					}
				} else {
					if (currentData.meals.isEmpty()) DefaultWideDivider()
					else {
						CartedGroupBox(indicatorText = dynamicTranslation("user.detail.mealsIntro")) {
							currentData.meals.forEach { meal ->
								ProfileDisplayRow(
									label = dynamicTranslation(mealWindows.first { it.id == meal.windowId }.nameKey),
									value = dynamicTranslation(meal.variantKey),
								)
							}
						}
					}
				}
				
				if (editMode.value) CustomDataEditor(fields = customDataEdit)
				else CustomDataDisplay(currentData.customData)
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
fun EditButtonsRow(
	editing: Boolean,
	onEditingChange: () -> Unit,
	onSave: () -> Unit,
	onRevoke: () -> Unit,
	onDelete: () -> Unit,
	onReissueCredentials: () -> Unit,
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
		
		Row(horizontalArrangement = Arrangement.spacedBy(defaultLayoutPadding)) {
			if (editing) {
				EditIcon(
					icon = Icons.Default.Key,
					background = colors.clickable,
					contentDesctiption = "reissue login credentials",
					onClick = onReissueCredentials,
				)
				EditIcon(
					icon = Icons.Default.Rotate90DegreesCw,
					background = colors.clickable,
					contentDesctiption = "revoke device",
					onClick = onRevoke,
				)
				EditIcon(
					icon = Icons.Default.Delete,
					background = colors.action.delete,
					contentDesctiption = "delete slot",
					onClick = onDelete,
				)
			}
		}
		
		Row(horizontalArrangement = Arrangement.spacedBy(defaultLayoutPadding)) {
			EditIcon(
				icon =
				if (editing) Icons.Default.Visibility
				else Icons.Default.Edit,
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
}
