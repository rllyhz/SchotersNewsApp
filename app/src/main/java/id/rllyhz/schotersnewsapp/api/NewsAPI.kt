package id.rllyhz.schotersnewsapp.api

import id.rllyhz.schotersnewsapp.data.responses.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("v2/top-headlines")
    suspend fun getTrendingNews(
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = ""
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = ""
    ): Response<NewsResponse>
}