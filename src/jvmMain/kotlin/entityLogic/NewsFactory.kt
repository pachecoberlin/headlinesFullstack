package entityLogic

import entities.News
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
            val dateStringClean = dateString.replace(" ", "").replace("-", "").trim()
            val datePatternClean = datePattern.replace(" ", "").trim()
            return News(
                title = title.trim(),
                url = url,
                provider = provider,
                overline = overline,
                teaser = teaser,
                text = text,
                breadcrumbs = breadcrumbs,
                author = author,
                displayDate = displayDate,
                dateString = dateStringClean,
                datePattern = datePatternClean
            )
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
            println("For provider: $provider")
            e.printStackTrace()
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