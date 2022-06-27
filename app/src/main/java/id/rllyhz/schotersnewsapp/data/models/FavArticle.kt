package id.rllyhz.schotersnewsapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "articles")
data class FavArticle(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val url: String,
    @ColumnInfo(name = "url_to_image")
    val urlToImage: String,
    val author: String?,
    val content: String?,
    val description: String,
    @ColumnInfo(name = "published_at")
    val publishedAt: String,
) : Serializable
