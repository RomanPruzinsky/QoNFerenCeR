package tr.qonferencer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.navigation.AppLayout
import tr.qonferencer.screens.splash.SplashScreen
import tr.qonferencer.screens.splash.SplashViewModel
import tr.qonferencer.screens.splash.splashViewModelFactory
import tr.qonferencer.theme.QoNFerenCeRTheme
import tr.qonferencer.trons.states.collectValue
import tr.qonferencer.trons.states.isSuccess

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		actionBar?.hide()
		setContent {
			val splashViewModel: SplashViewModel =
				viewModel(factory = splashViewModelFactory(QoNFerenCerApi.splash))
			
			QoNFerenCeRTheme {
				if (splashViewModel.splashState.collectValue().isSuccess()) AppLayout()
				else SplashScreen()
			}
		}
	}
}
