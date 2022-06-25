package id.rllyhz.schotersnewsapp.data.source

import androidx.lifecycle.LiveData
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.data.source.local.LocalDataSource
import id.rllyhz.schotersnewsapp.data.source.remote.RemoteDataSource
import id.rllyhz.schotersnewsapp.utils.Resource

class NewsRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : NewsDataSource {
    override suspend fun getTrendingNews(): LiveData<Resource<List<Article>>> =
        remoteDataSource.getTrendingNews()

    override suspend fun searchNews(query: String): LiveData<Resource<List<Article>>> =
        remoteDataSource.searchNews(query)

    override fun getAllFavNews(): LiveData<List<FavArticle>> =
        localDataSource.getAllFavNews()

    override suspend fun insertOrUpdateFavNews(favNews: FavArticle): Long =
        localDataSource.insertOrUpdateFavNews(favNews)

    override suspend fun deleteFavNews(favNews: FavArticle) =
        localDataSource.deleteFavNews(favNews)

    companion object {
        private var instance: NewsRepository? = null

        fun getInstance(
            remoteDataSource: RemoteDataSource,
            localDataSource: LocalDataSource
        ): NewsRepository =
            (instance ?: synchronized(this) {
                instance ?: NewsRepository(remoteDataSource, localDataSource)
            })
    }
}