package entityLogic

import entities.News
import io.ktor.util.*
import java.time.LocalDateTime

class NewsFactory {
    companion object {
        fun createNews(
            title: String = "",
            url: String = "",
            provider: String = "",
            overline: String = "",
            teaser: String = "",
            text: String = "",
            breadcrumbs: List<String> = emptyList(),
            author: String = "",
            displayDate: String = "",
            dateString: String = "",
            datePattern: String = ""
        ): News {
            val dateStringClean = dateString.replace(" ", "").replace("-", "").replace("Uhr","").trim()
            val datePatternClean = datePattern.replace(" ", "").trim()
            val news = News(
                title = title.trim(),
                url = url,
                provider = provider,
                overline = overline,
                teaser = teaser,
                text = text,
                breadcrumbs = breadcrumbs,
                author = author,
                datePattern = datePatternClean
            )
            news.dateString = dateStringClean
            news.displayDate = displayDate
            return news
        }
    }
}

val News.date: LocalDateTime
    get() = dateCache

private val News.dateCache: LocalDateTime
    get() {
        return try {
            NewsTime.createLocalDateTime(datePattern, dateString)
        } catch (e: Exception) {
            if (dateString.isNotEmpty()) {
                println("For provider: $provider")
                e.printStackTrace()
            }
            NewsTime.fallBackDateTime
        }
    }

/**
 * News are relevant when they are no older than 24 hours.
 */
val News.relevant: Boolean
    get() {
        val now = LocalDateTime.now()
        return date.isAfter(now.minusHours(24)) && date.isBefore(now)
    }