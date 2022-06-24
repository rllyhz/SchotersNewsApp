package id.rllyhz.schotersnewsapp.data.source

import androidx.lifecycle.LiveData
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.data.source.remote.RemoteDataSource
import id.rllyhz.schotersnewsapp.utils.Resource

class NewsRepository(
    private val remoteDataSource: RemoteDataSource
) : NewsDataSource {
    override suspend fun getTrendingNews(): LiveData<Resource<List<Article>>> =
        remoteDataSource.getTrendingNews()

    override suspend fun searchNews(query: String): LiveData<Resource<List<Article>>> =
        remoteDataSource.searchNews(query)

    companion object {
        private var instance: NewsRepository? = null

        fun getInstance(
            remoteDataSource: RemoteDataSource
        ): NewsRepository =
            (instance ?: synchronized(this) {
                instance ?: NewsRepository(remoteDataSource)
            })
    }
}