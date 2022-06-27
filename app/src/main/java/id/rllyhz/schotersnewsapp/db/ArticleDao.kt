package id.rllyhz.schotersnewsapp.db

import androidx.room.*
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY id DESC")
    fun getAllFavNews(): Flow<List<FavArticle>>

    @Query("SELECT * FROM articles WHERE title = :title AND published_at = :publishedAt AND url = :url AND url_to_image = :urlToImage")
    fun getFavNews(
        title: String,
        publishedAt: String,
        url: String,
        urlToImage: String,
    ): FavArticle?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(news: FavArticle): Long

    @Delete
    fun deleteNews(news: FavArticle): Int
}