package id.rllyhz.schotersnewsapp.ui.detail

import androidx.lifecycle.*
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.data.source.NewsRepository
import id.rllyhz.schotersnewsapp.utils.DispatcherProvider
import id.rllyhz.schotersnewsapp.utils.Event
import id.rllyhz.schotersnewsapp.utils.asLocalModel
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: NewsRepository,
    private val dispatchers: DispatcherProvider,
    private val news: Article
) : ViewModel() {
    private val _showMessageEvent = MutableLiveData<Event<String>>()
    val showMessageEvent: LiveData<Event<String>> get() = _showMessageEvent

    val isFavorite = MutableLiveData(false)
    private var _favNews: FavArticle? = null

    init {
        viewModelScope.launch(dispatchers.io) {
            _favNews = repository.getFavNewsOf(news)
            isFavorite.postValue(_favNews != null)
        }
    }

    fun addOrDeleteFromFav(news: Article, addedAndDeletedMessages: List<String>) {
        isFavorite.value?.let { favorite ->

            viewModelScope.launch(dispatchers.io) {
                if (favorite) {
                    _favNews?.let { favNews ->
                        val result = repository.deleteFavNews(favNews)

                        // success
                        if (result > 0) {
                            _showMessageEvent.postValue(Event(addedAndDeletedMessages[2]))
                            isFavorite.postValue(false)
                        } else {
                            _showMessageEvent.postValue(Event(addedAndDeletedMessages[3]))
                            isFavorite.postValue(true)
                        }
                    }
                } else {
                    val result = repository.insertOrUpdateFavNews(news.asLocalModel())

                    // success
                    if (result.toInt() > 0) {
                        _showMessageEvent.postValue(Event(addedAndDeletedMessages[0]))
                        isFavorite.postValue(true)

                        // updated the newest added favorite one
                        _favNews = repository.getFavNewsOf(news)
                    } else {
                        _showMessageEvent.postValue(Event(addedAndDeletedMessages[1]))
                        isFavorite.postValue(false)
                    }
                }
            }
        }
    }

    class Factory(
        private val repository: NewsRepository,
        private val dispatchers: DispatcherProvider,
        private val news: Article
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetailViewModel(repository, dispatchers, news) as T
    }
}