package id.rllyhz.schotersnewsapp.ui.detail

import androidx.lifecycle.*
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.data.source.NewsRepository
import id.rllyhz.schotersnewsapp.utils.DispatcherProvider
import id.rllyhz.schotersnewsapp.utils.Event
import id.rllyhz.schotersnewsapp.utils.asLocalModel
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: NewsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {
    private val _showMessageEvent = MutableLiveData<Event<String>>()
    val showMessageEvent: LiveData<Event<String>> get() = _showMessageEvent

    var isFavorite = MutableLiveData(false)

    fun addOrDeleteFromFav(news: Article, addedAndDeletedMessages: List<String>) {
        val favorite = isFavorite.value!!

        if (favorite) {
            viewModelScope.launch(dispatchers.io) { repository.deleteFavNews(news.asLocalModel()) }
            _showMessageEvent.value =
                Event(addedAndDeletedMessages[1])
            isFavorite.value = false
        } else {
            viewModelScope.launch(dispatchers.io) { repository.insertOrUpdateFavNews(news.asLocalModel()) }
            _showMessageEvent.value =
                Event(addedAndDeletedMessages[0])
            isFavorite.value = true
        }
    }

    fun addOrDeleteFromFav() {
        val favorite = isFavorite.value!!
        isFavorite.value = !favorite
    }

    class Factory(
        private val repository: NewsRepository,
        private val dispatchers: DispatcherProvider
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetailViewModel(repository, dispatchers) as T
    }
}