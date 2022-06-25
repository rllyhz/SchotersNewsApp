package id.rllyhz.schotersnewsapp.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.rllyhz.schotersnewsapp.data.source.NewsRepository

class DetailViewModel(
    private val repository: NewsRepository
) : ViewModel() {
    var isFavorite = MutableLiveData(false)

    fun addOrDeleteFromFav() {
        val favorite = isFavorite.value!!
        isFavorite.value = !favorite
    }

    class Factory(private val repository: NewsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetailViewModel(repository) as T
    }
}