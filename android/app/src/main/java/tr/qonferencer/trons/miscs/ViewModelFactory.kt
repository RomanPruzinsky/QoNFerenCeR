package tr.qonferencer.trons.miscs

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Factory for [ViewModel] with [T] as only constructor
 * @param param [T] value to construct with
 */
class ParamViewModelFactory<T>(
	private val param: T,
) : ViewModelProvider.Factory {
	override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
		val constructor = modelClass.getConstructor(param!!::class.java)
		return constructor.newInstance(param)
	}
}

/**
 * Factory for [ViewModel] with [Int] as only constructor
 * @param id [Int] value to construct with
 */
class IntIdViewModelFactory(
	private val id: Int,
) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T =
		modelClass.getConstructor(Int::class.java).newInstance(id)
}

/**
 * Get [ViewModel] with [Int] as only constructor
 * @param id [Int] identifier
 */
@Composable
inline fun <reified T : ViewModel> initIndIdViewModel(id: Int) = viewModel<T>(
	key = id.toString(),
	factory = IntIdViewModelFactory(id),
)
