package id.rllyhz.schotersnewsapp.ui.features.favorites

import android.util.Log
import androidx.lifecycle.*
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.data.source.NewsRepository
import id.rllyhz.schotersnewsapp.utils.DispatcherProvider
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: NewsRepository,
    dispatchers: DispatcherProvider
) : ViewModel() {
    private var _favNews: LiveData<List<FavArticle>> = MutableLiveData()
    val favNews: LiveData<List<FavArticle>> = _favNews

    fun getAllNews() = repository.getAllFavNews()

    init {
        Log.d("myapp", "Init fav viewmodel")
        viewModelScope.launch(dispatchers.io) {
            //val result = repository.getAllFavNews()
            //_favNews = result
        }
    }

    class Factory(
        private val repository: NewsRepository,
        private val dispatchers: DispatcherProvider
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FavoritesViewModel(repository, dispatchers) as T

    }
}