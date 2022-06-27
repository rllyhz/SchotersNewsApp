package id.rllyhz.schotersnewsapp.data.source.local

import androidx.lifecycle.LiveData
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.db.ArticleDao

class LocalDataSource constructor(
    private val newsDao: ArticleDao
) {
    fun getAllFavNews(): LiveData<List<FavArticle>> =
        newsDao.getAllNews()

    fun insertOrUpdateFavNews(news: FavArticle): Long =
        newsDao.insertOrUpdate(news)

    fun deleteFavNews(news: FavArticle): Int =
        newsDao.deleteNews(news)

    companion object {
        @Volatile
        private var localDataSource: LocalDataSource? = null

        fun getInstance(dao: ArticleDao): LocalDataSource =
            localDataSource ?: synchronized(this) {
                localDataSource ?: LocalDataSource(dao)
            }
    }
}