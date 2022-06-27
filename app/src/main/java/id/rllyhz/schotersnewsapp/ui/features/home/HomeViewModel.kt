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
) : ViewModel() {

    suspend fun getNews() = repository.getTrendingNews()

    class Factory(
        private val repository: NewsRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repository) as T
    }
}