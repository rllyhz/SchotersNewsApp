package id.rllyhz.schotersnewsapp.data.source.local

import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.db.ArticleDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource constructor(
    private val newsDao: ArticleDao
) {
    fun getAllFavNews(): Flow<List<FavArticle>> =
        newsDao.getAllFavNews()

    fun getFavNewsById(favNews: FavArticle): FavArticle? =
        newsDao.getFavNews(
            favNews.title,
            favNews.publishedAt, favNews.url, favNews.urlToImage
        )

    fun insertOrUpdateFavNews(favNews: FavArticle): Long =
        newsDao.insertOrUpdate(favNews)

    fun deleteFavNews(favNews: FavArticle): Int =
        newsDao.deleteNews(favNews)

    companion object {
        @Volatile
        private var localDataSource: LocalDataSource? = null

        fun getInstance(dao: ArticleDao): LocalDataSource =
            localDataSource ?: synchronized(this) {
                localDataSource ?: LocalDataSource(dao)
            }
    }
}