package entityLogic

import entities.News
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NewsFactory {
    companion object {
        @Suppress("UNUSED_PARAMETER")
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
            var dateStringClean = dateString.trim()
            listOf("Uhr", "Min.", " ", "-", "vor")
                .forEach { dateStringClean = dateStringClean.replace(it, "") }
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
            news.displayDate = news.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMAN)).toString()
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