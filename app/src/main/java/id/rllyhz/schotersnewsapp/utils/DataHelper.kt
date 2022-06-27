package id.rllyhz.schotersnewsapp.utils

import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.data.models.FavArticle

fun formalizeDate(dateString: String): String {
    if (dateString.isEmpty()) return dateString

    val delimiter = "T"

    // split date and time
    val parts = dateString.split(delimiter)
    val date = parts[0]
    val time = parts[1]

    return "${getCorrectDate(date)} - ${getCorrectTime(time)}"
}

// removing last character (z)
private fun getCorrectTime(time: String): String =
    time.dropLast(1)

// split date, month and year
private fun getCorrectDate(date: String): String {
    val delimiter = "-"
    val parts = date.split(delimiter)
    val year = parts[0]
    val month = parts[1]
    val currDate = parts[2]

    return "$currDate ${getCorrectMonth(month)} $year"
}

// change number-month into named-month
private fun getCorrectMonth(month: String): String =
    when (month.toInt()) {
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "Mei"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> "Jan"
    }

fun Article.asLocalModel(): FavArticle =
    FavArticle(
        title = title,
        url = url,
        urlToImage = urlToImage,
        author = author,
        content = content,
        description = description,
        publishedAt = publishedAt,
    )

fun FavArticle.asModel(): Article =
    Article(
        title = title,
        url = url,
        urlToImage = urlToImage,
        author = author,
        content = content,
        description = description,
        publishedAt = publishedAt,
    )