package id.rllyhz.schotersnewsapp.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.rllyhz.schotersnewsapp.api.NewsAPI
import id.rllyhz.schotersnewsapp.api.RetrofitInstance
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.utils.Resource

class RemoteDataSource constructor(
    private val newsAPI: NewsAPI
) {
    suspend fun getTrendingNews(): LiveData<Resource<List<Article>>> {
        val results = MutableLiveData<Resource<List<Article>>>(Resource.Initial())

        results.postValue(Resource.Loading())

        try {
            val response = newsAPI.getTrendingNews()
            val bodyResponse = response.body()

            if (response.isSuccessful && bodyResponse != null) {
                val data = Resource.Success(bodyResponse.articles)
                results.postValue(data)
            } else {
                results.postValue(Resource.Error(response.message()))
            }
        } catch (exc: Exception) {
            results.postValue(Resource.Error("Something went wrong"))
        }

        return results
    }

    suspend fun searchNews(query: String): LiveData<Resource<List<Article>>> {
        val results = MutableLiveData<Resource<List<Article>>>(Resource.Initial())

        results.postValue(Resource.Loading())

        try {
            val response = newsAPI.searchNews(query)
            val bodyResponse = response.body()

            if (response.isSuccessful && bodyResponse != null) {
                val data = Resource.Success(bodyResponse.articles)
                results.postValue(data)
            } else {
                results.postValue(Resource.Error(response.message()))
            }
        } catch (exc: Exception) {
            results.postValue(Resource.Error("Something went wrong"))
        }

        return results
    }

    companion object {
        @Volatile
        private var remoteDataSource: RemoteDataSource? = null

        fun getInstance(): RemoteDataSource =
            remoteDataSource ?: synchronized(this) {
                remoteDataSource ?: RemoteDataSource(RetrofitInstance.newsApi)
            }
    }
}