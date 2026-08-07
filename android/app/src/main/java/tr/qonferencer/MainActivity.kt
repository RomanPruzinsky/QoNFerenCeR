package tr.qonferencer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.screens.splash.SplashScreen
import tr.qonferencer.screens.splash.SplashViewModel
import tr.qonferencer.screens.splash.splashViewModelFactory
import tr.qonferencer.theme.QoNFerenCeRTheme
import tr.qonferencer.trons.states.dataState.DataState

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		actionBar?.hide()
		setContent {
			val splashViewModel: SplashViewModel =
				viewModel(factory = splashViewModelFactory(QoNFerenCerApi.splash))
			val splashState by splashViewModel.splashState.collectAsState()
			
			QoNFerenCeRTheme {
				when (splashState) {
					is DataState.Success -> Unit
					else -> SplashScreen()
				}
			}
		}
	}
}
