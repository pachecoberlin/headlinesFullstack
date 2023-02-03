package entityLogic

import entities.News
import entityLogic.NewsTime.Companion.dateTimePattern
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NewsFactory {
    companion object {
        fun createNews(
            title: String = "",
            url: String = "",
            provider: String = "",
            overline: String = "",
            teaser: String = "",
            dateString: String = "",
            datePattern: String = "",
            author: String = "",
            source: String = "",
            text: String = "",
            breadcrumbs: List<String> = emptyList(),
            @Suppress("UNUSED_PARAMETER") displayDate: String = "",
        ): News {
            var dateStringClean = dateString.trim()
            var datePatternClean = datePattern.trim()
            listOf("Uhr", "Min.", " ", "-", "vor", "\n", "Stand:")
                .forEach {
                    dateStringClean = dateStringClean.replace(it, "")
                    datePatternClean = datePatternClean.replace(it, "")
                }
            val news = News(
                title = title.trim(),
                url = url,
                provider = provider,
                overline = overline,
                teaser = teaser,
                text = text,
                breadcrumbs = breadcrumbs,
                author = author,
                datePattern = datePatternClean,
                dateString = dateStringClean,
                source = source,
            )
            news.displayDate = news.date.format(DateTimeFormatter.ofPattern(dateTimePattern, Locale.GERMAN)).toString()
            news.datePattern = dateTimePattern
            news.dateString = news.displayDate
            return news
        }
    }
}

private val News.date: LocalDateTime
    get() = try {
        NewsTime.createLocalDateTime(datePattern, dateString)
    } catch (e: Exception) {
        if (dateString.isNotEmpty()) {
            println("For provider: $provider")
            println(e.localizedMessage)
            e.printStackTrace()
        }
        NewsTime.fallBackDateTime
    }

/**
 * News are relevant when they are no older than 24 hours.
 */
val News.relevant: Boolean
    get() {
        val now = LocalDateTime.now()
        return date.isAfter(now.minusHours(24)) && date.isBefore(now)
    }


fun News.updateDisplayDate(dateString: String, datePattern: String) {
    this.dateString=dateString
    this.datePattern=datePattern
    displayDate = date.format(DateTimeFormatter.ofPattern(dateTimePattern, Locale.GERMAN)).toString()
    this.datePattern = dateTimePattern
    this.dateString = displayDate
}