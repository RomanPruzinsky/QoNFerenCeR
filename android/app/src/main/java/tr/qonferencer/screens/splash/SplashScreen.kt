package tr.qonferencer.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.QoNFerenCeRApp
import tr.qonferencer.R
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.shared.dtos.UserDetailDto
import tr.qonferencer.theme.color
import tr.qonferencer.theme.colors
import tr.qonferencer.theme.typo
import tr.qonferencer.trons.defaultLayouts.DefaultHeightSpacer
import tr.qonferencer.trons.defaultLayouts.defaultSpacing
import tr.qonferencer.trons.miscs.DefaultSay
import tr.qonferencer.trons.miscs.ENDL
import tr.qonferencer.trons.miscs.getEventId
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.theme.defaultBorder
import tr.qonferencer.trons.theme.defaultClip
import tr.qonferencer.trons.theme.defaultClipSize
import tr.qonferencer.trons.theme.defaultLayoutPadding
import tr.qonferencer.trons.theme.defaultTextPadding

@Composable
fun SplashScreen(viewModel: SplashViewModel = viewModel(factory = splashViewModelFactory(QoNFerenCerApi.splash))) {
	val state by viewModel.splashState.collectAsState()
	val currentLevel = UserDetailDto.roleOrAnonym(QoNFerenCeRApp.currentUser.details.collectValue())
	val eventId = getEventId(LocalContext.current)

	Box(
		modifier = Modifier
			.fillMaxSize()
			.windowInsetsPadding(WindowInsets.systemBars),
	) {
		CornerColorBox(currentLevel.color, Alignment.TopStart)
		CornerColorBox(colors.navigation, Alignment.BottomStart)

		Column(
			modifier = Modifier
				.fillMaxSize()
				.defaultClip()
				.background(colors.appBackground)
				.defaultLayoutPadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Text(
				text = eventId.replace("_", " "),
				style = typo.displayMedium,
				modifier = Modifier.defaultTextPadding(),
			)

			DefaultHeightSpacer(2)

			when (val current = state) {
				DataState.Waiting, DataState.Processing ->
					CircularProgressIndicator(
						modifier = Modifier
							.fillMaxWidth(.6F)
							.aspectRatio(1F),
					)

				is DataState.Success -> Unit // Will automatically switch
				is DataState.Error -> {
					Column(
						modifier = Modifier
							.defaultBorder()
							.defaultLayoutPadding(),
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = defaultSpacing,
					) {
						Text(
							text = DefaultSay.ERROR + ENDL + current.specification?.message,
							style = typo.bodyMedium,
						)
						Text(
							text = DefaultSay.REFRESH,
							style = typo.labelLarge,
							modifier = Modifier
								.defaultClip()
								.clickable { viewModel.load() }
								.background(colors.clickable)
								.defaultTextPadding(),
						)
					}
				}
			}

			DefaultHeightSpacer(2)

			Image(
				painter = painterResource(R.drawable.logo),
				contentDescription = null,
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.defaultLayoutPadding()
					.fillMaxWidth(.7F)
					.aspectRatio(1F)
					.defaultClip()
					.background(colors.container),
			)
		}
	}
}

@Composable
private fun BoxScope.CornerColorBox(
	color: Color,
	alignment: Alignment,
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(defaultClipSize)
			.background(color)
			.align(alignment),
	)
}
