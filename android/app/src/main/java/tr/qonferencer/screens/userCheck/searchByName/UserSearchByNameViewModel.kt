package tr.qonferencer.screens.userCheck.searchByName

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tr.qonferencer.api.QoNFerenCerApi
import tr.qonferencer.shared.dtos.PageDto
import tr.qonferencer.shared.dtos.UserDisplayDto
import tr.qonferencer.trons.miscs.EMPTY_STRING
import tr.qonferencer.trons.states.dataState.DataState
import tr.qonferencer.trons.states.dataState.dataStatedAction
import tr.qonferencer.trons.states.dataState.initDataState
import tr.qonferencer.trons.states.dataState.success
import tr.qonferencer.trons.states.dataState.waiting
import tr.qonferencer.trons.states.isNotSuccess
import tr.qonferencer.trons.viewmodels.corout

/** Loads paged name-search results */
class UserSearchByNameViewModel : ViewModel() {
	private val _searchState = initDataState<PageDto<UserDisplayDto>>()
	val searchState = _searchState.asStateFlow()
	
	private val _loadingMore = MutableStateFlow(false)
	val loadingMore = _loadingMore.asStateFlow()
	
	private var query = EMPTY_STRING
	
	fun searchByName(query: String) {
		this.query = query
		dataStatedAction(_searchState) { QoNFerenCerApi.user.searchByName(searchFor = query, page = 0) }
	}
	
	fun loadNextPage() {
		if (searchState.isNotSuccess()) return
		val loaded = (searchState.value as DataState.Success).value
		if (loadingMore.value || loaded.number + 1 >= loaded.totalPages) return
		
		corout {
			_loadingMore.value = true
			try {
				val next = QoNFerenCerApi.user.searchByName(searchFor = query, page = loaded.number + 1)
				_searchState.success(next.copy(content = loaded.content + next.content))
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				_loadingMore.value = false
			}
		}
	}

	/** Discards search results */
	fun resetSearch() {
		_searchState.waiting()
	}
}
