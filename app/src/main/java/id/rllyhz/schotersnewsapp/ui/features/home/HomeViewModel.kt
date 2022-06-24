package id.rllyhz.schotersnewsapp.ui.features.home

import androidx.lifecycle.*
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.data.source.NewsRepository
import id.rllyhz.schotersnewsapp.utils.DispatcherProvider
import id.rllyhz.schotersnewsapp.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: NewsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {
    private var _news: MutableLiveData<List<Article>?> =
        MutableLiveData(null)
    val news: LiveData<List<Article>?> = _news

    val shouldLoading = MutableLiveData(false)
    val isError = MutableLiveData(false)

    fun getTrendingNews() = viewModelScope.launch(dispatchers.io) {
        val result = repository.getTrendingNews()

        when (result.value) {
            is Resource.Loading -> withContext(dispatchers.main) {
                shouldLoading.value = true
                isError.value = false
                _news.value = result.value?.data
            }
            is Resource.Error -> withContext(dispatchers.main) {
                shouldLoading.value = false
                isError.value = true
                _news.value = result.value?.data
            }
            is Resource.Success -> withContext(dispatchers.main) {
                shouldLoading.value = false
                isError.value = false
                _news.value = result.value!!.data
            }
            else -> Unit
        }
    }

    class Factory(
        private val repository: NewsRepository,
        private val dispatchers: DispatcherProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repository, dispatchers) as T
    }
}