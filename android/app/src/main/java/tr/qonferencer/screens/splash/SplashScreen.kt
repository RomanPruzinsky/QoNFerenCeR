package tr.qonferencer.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.trons.states.dataState.DataState

@Composable
fun SplashScreen(
	modifier: Modifier = Modifier,
	viewModel: SplashViewModel = viewModel(factory = splashViewModelFactory(QoNFerenCerApi.splash)),
) {
	val state by viewModel.splashState.collectAsState()
	//TODO: not programmed by me

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		when (val current = state) {
			DataState.Waiting, DataState.Processing -> CircularProgressIndicator()
			is DataState.Error -> {
				Text("Failed to load: ${current.specification?.message}")
				Button(onClick = viewModel::load) { Text("Retry") }
			}
			is DataState.Success -> Unit
		}
	}
}
