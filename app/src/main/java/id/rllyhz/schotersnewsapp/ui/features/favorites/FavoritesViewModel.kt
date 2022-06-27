package id.rllyhz.schotersnewsapp.ui.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.data.source.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: NewsRepository
) : ViewModel() {
    fun getAllFavNews() = repository.getAllFavNews()

    fun deleteFavNews(favNews: FavArticle) =
        viewModelScope.launch(Dispatchers.IO) { repository.deleteFavNews(favNews) }

    class Factory(
        private val repository: NewsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FavoritesViewModel(repository) as T

    }
}