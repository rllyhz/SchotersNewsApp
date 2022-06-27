package id.rllyhz.schotersnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import id.rllyhz.schotersnewsapp.data.models.FavArticle

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles order by id")
    fun getAllNews(): LiveData<List<FavArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(news: FavArticle): Long

    @Delete
    fun deleteNews(news: FavArticle): Int
}