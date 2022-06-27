package id.rllyhz.schotersnewsapp.ui.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.rllyhz.schotersnewsapp.data.source.NewsRepository

class FavoritesViewModel(
    private val repository: NewsRepository
) : ViewModel() {
    fun getAllNews() = repository.getAllFavNews()

    class Factory(
        private val repository: NewsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FavoritesViewModel(repository) as T

    }
}