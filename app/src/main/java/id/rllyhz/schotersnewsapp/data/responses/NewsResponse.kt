package id.rllyhz.schotersnewsapp.data.responses

import id.rllyhz.schotersnewsapp.data.models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)