package id.rllyhz.schotersnewsapp.ui.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.rllyhz.schotersnewsapp.data.source.NewsRepository

class SearchViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    suspend fun searchNews(queryString: String) =
        repository.searchNews(queryString)

    class Factory(
        private val repository: NewsRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SearchViewModel(repository) as T
    }
}