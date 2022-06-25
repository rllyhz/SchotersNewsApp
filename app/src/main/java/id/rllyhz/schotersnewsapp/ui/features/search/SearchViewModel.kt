package id.rllyhz.schotersnewsapp.ui.features.search

import androidx.lifecycle.*
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.data.source.NewsRepository
import id.rllyhz.schotersnewsapp.ui.features.home.HomeViewModel
import id.rllyhz.schotersnewsapp.utils.DispatcherProvider
import id.rllyhz.schotersnewsapp.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val repository: NewsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {
    private val _searchResult: MutableLiveData<List<Article>?> = MutableLiveData(null)
    val searchNews: LiveData<List<Article>?> = _searchResult

    val shouldLoading = MutableLiveData(false)
    val isError = MutableLiveData(false)

    fun searchNews(queryString: String) = viewModelScope.launch(dispatchers.io) {
        val result = repository.searchNews(queryString)

        when (result.value) {
            is Resource.Loading -> withContext(dispatchers.main) {
                shouldLoading.value = true
                isError.value = false
                _searchResult.value = result.value?.data
            }
            is Resource.Error -> withContext(dispatchers.main) {
                shouldLoading.value = false
                isError.value = true
                _searchResult.value = result.value?.data
            }
            is Resource.Success -> withContext(dispatchers.main) {
                shouldLoading.value = false
                isError.value = false
                _searchResult.value = result.value!!.data
            }
            else -> Unit
        }
    }

    class Factory(
        private val repository: NewsRepository,
        private val dispatchers: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SearchViewModel(repository, dispatchers) as T
    }
}