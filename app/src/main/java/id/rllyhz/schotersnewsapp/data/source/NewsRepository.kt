package id.rllyhz.schotersnewsapp.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.data.source.local.LocalDataSource
import id.rllyhz.schotersnewsapp.data.source.remote.RemoteDataSource
import id.rllyhz.schotersnewsapp.utils.DispatcherProvider
import id.rllyhz.schotersnewsapp.utils.Resource
import id.rllyhz.schotersnewsapp.utils.asLocalModel
import kotlinx.coroutines.withContext

class NewsRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatcherProvider: DispatcherProvider
) : NewsDataSource {
    override suspend fun getTrendingNews(): LiveData<Resource<List<Article>>> =
        remoteDataSource.getTrendingNews()

    override suspend fun searchNews(query: String): LiveData<Resource<List<Article>>> =
        remoteDataSource.searchNews(query)

    override fun getAllFavNews(): LiveData<List<FavArticle>> =
        localDataSource.getAllFavNews().asLiveData()

    override suspend fun getFavNewsOf(news: Article): FavArticle? =
        localDataSource.getFavNewsById(news.asLocalModel())

    override suspend fun insertOrUpdateFavNews(favNews: FavArticle): Long =
        withContext(dispatcherProvider.io) {
            localDataSource.insertOrUpdateFavNews(
                favNews
            )
        }

    override suspend fun deleteFavNews(favNews: FavArticle): Int =
        withContext(dispatcherProvider.io) {
            localDataSource.deleteFavNews(favNews)
        }

    companion object {
        private var instance: NewsRepository? = null

        fun getInstance(
            remoteDataSource: RemoteDataSource,
            localDataSource: LocalDataSource,
            dispatcherProvider: DispatcherProvider
        ): NewsRepository =
            (instance ?: synchronized(this) {
                instance ?: NewsRepository(remoteDataSource, localDataSource, dispatcherProvider)
            })
    }
}