package id.rllyhz.schotersnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import id.rllyhz.schotersnewsapp.data.models.FavArticle

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles")
    fun getAllNews(): LiveData<List<FavArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(news: FavArticle): Long

    @Delete
    suspend fun deleteNews(news: FavArticle)
}