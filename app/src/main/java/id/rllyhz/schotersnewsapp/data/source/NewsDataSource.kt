package id.rllyhz.schotersnewsapp.data.source

import androidx.lifecycle.LiveData
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.utils.Resource

interface NewsDataSource {
    suspend fun getTrendingNews(): LiveData<Resource<List<Article>>>
    suspend fun searchNews(query: String): LiveData<Resource<List<Article>>>
    fun getAllFavNews(): LiveData<List<FavArticle>>
    suspend fun getFavNewsOf(news: Article): FavArticle?
    suspend fun insertOrUpdateFavNews(favNews: FavArticle): Long
    suspend fun deleteFavNews(favNews: FavArticle): Int
}