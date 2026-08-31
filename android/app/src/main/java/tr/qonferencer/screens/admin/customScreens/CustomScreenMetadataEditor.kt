package tr.qonferencer.screens.admin.customScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import tr.qonferencer.navigation.iconFrom
import tr.qonferencer.navigation.navIcons
import tr.qonferencer.shared.dtos.CustomScreenAdminDto
import tr.qonferencer.shared.enums.Role
import tr.qonferencer.theme.colors
import tr.qonferencer.translations.dynamicTranslation
import tr.qonferencer.trons.defaultLayouts.CustomDropdownMenu
import tr.qonferencer.trons.defaultLayouts.DefaultOTF
import tr.qonferencer.trons.defaultLayouts.ProfileEditRow
import tr.qonferencer.trons.defaultLayouts.ScrollableColumn
import tr.qonferencer.trons.defaultLayouts.TransparentGroupBox
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.DEFAULT_STATE_CHANGE_DELAY_SECS
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.ops.relist
import tr.qonferencer.trons.remembers.rememberFalse
import tr.qonferencer.trons.remembers.rememberString
import tr.qonferencer.trons.states.StateIndicator
import tr.qonferencer.trons.states.dataState.DataStateLayout
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultIconSize
import tr.qonferencer.trons.theme.defaultIconSizeLarge
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding
import tr.qonferencer.trons.theme.orTransparentIf

@Composable
fun CustomScreenMetadataEditor(screen: CustomScreenAdminDto) {
	val screensVM = viewModel<CustomScreensViewModel>()

	val titleKey = rememberString(screen.titleKey)
	val navIcon = rememberString(screen.icon)
	val roleIndex = remember { mutableIntStateOf(screen.minRole.ordinal) }

	DataStateLayout(
		stateFlow = screensVM.saveState,
		bodyOnWaiting = {
			ScrollableColumn(
				modifier = Modifier
					.fillMaxSize()
					.defaultLayoutPadding(),
				verticalArrangement = defaultSpacing,
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				DefaultOTF(
					valueText = titleKey,
					labelText = dynamicTranslation("admin.customScreen.titleKey"),
					modifier = Modifier.fillMaxWidth(),
				)

				TransparentGroupBox(
					indicatorText = dynamicTranslation("admin.customScreen.icon"),
					modifier = Modifier.fillMaxWidth(),
				) {
					FlowRow(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = defaultSpacing,
						verticalArrangement = defaultSpacing,
					) {
						navIcons.keys.forEach { key ->
							Icon(
								imageVector = iconFrom(key),
								contentDescription = key,
								tint = colors.text,
								modifier = Modifier
									.defaultClip()
									.background(colors.clickable.orTransparentIf(key != navIcon.value))
									.clickable { navIcon.value = key }
									.defaultLayoutPadding()
									.size(defaultIconSize),
							)
						}
					}
				}
				ProfileEditRow(dynamicTranslation("admin.customScreen.minRole")) {
					CustomDropdownMenu(
						options = Role.entries.relist { it.name },
						selected = roleIndex,
						expanded = rememberFalse(),
						arrowAtStart = false,
						selectedColor = colors.clickable,
					)
				}

				Icon(
					imageVector = Icons.Default.Check,
					contentDescription = "save custom screen metadata",
					tint = colors.text,
					modifier = Modifier
						.align(Alignment.End)
						.defaultClip()
						.background(colors.action.approve)
						.clickable {
							screensVM.save(
								screen.copy(
									titleKey = titleKey.value.trim(),
									icon = navIcon.value,
									minRole = Role.fromIndex(roleIndex.intValue),
								),
							)
						}
						.defaultTextPadding()
						.size(defaultIconSizeLarge),
				)
			}
		},
		bodyOnSuccess = { saved ->
			StateIndicator(text = DefaultSay.SUCCESS)
			LaunchedEffect(saved) {
				delay(DEFAULT_STATE_CHANGE_DELAY_SECS)
				screensVM.resetSaveState()
			}
		},
	)
}
