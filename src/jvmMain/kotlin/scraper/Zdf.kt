package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.nodes.Element
import utilities.getStaticContentFromUrl

class Zdf : Scraper {
    override val htmlClass: String = "container"
    override val tagName = ""
    override val url: String = "https://www.zdf.de/nachrichten/nachrichtenticker-100.html"

    override suspend fun getNews(newsList: MutableList<News>): List<News> {
        delay(delay * 10)
        return super.getNews(newsList)
    }

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        val newsContainer = element.getElementsByClass(htmlClass)
        if (newsContainer.size > 1) {
            newsContainer.forEach { parse(it, newsList) }
            println("Unexpected number of tags: ${newsContainer.size}")
            return
        } else if (newsContainer.isEmpty()) {
            System.err.println("No tag with class:$htmlClass in here")
            return
        }
        val newsEntry = newsContainer.first()
        val overline = newsEntry?.getElementsByClass("teaser-cat-category")?.first()?.text() ?: ""
        val title = newsEntry?.getElementsByClass("normal-space")?.first()?.wholeOwnText() ?: ""
        val text = newsEntry?.getElementsByClass("panel-content")?.first()?.wholeOwnText() ?: ""
        val time = newsEntry?.getElementsByClass("teaser-time")?.first()?.wholeOwnText() ?: ""
        var displayDate = ""
        val previousSibling = element.parent()?.parent()?.previousElementSibling()
        previousSibling?.let {
            displayDate = it.wholeOwnText()
            println()
        }
        val date = displayDate.trim().removePrefix("Gestern").removePrefix("Heute")
        val news = NewsFactory.createNews(
            title = title,
            provider = "ZDF",
            overline = overline,
            text = text,
            displayDate = displayDate,
            dateString = "$time$date",
            datePattern = "HH:mm[,dd.MM.yyyy]",
        )
        if (news.relevant) newsList.add(news)
    }
}

class ZdfWirtschaft : Scraper {
    override val htmlClass: String = "b-content-teaser-list"
    override val tagName: String = ""
    override val url: String = "https://www.zdf.de/nachrichten/wirtschaft"

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        if (element.select(".section-header-title").first()?.wholeOwnText()?.contains("Aktuelle Wirtschaftsnachrichten") == true)
            element.getElementsByTag("article").forEach { article ->
                delay(delay)
                val anchor = article.getElementsByTag("a")
                val url = anchor.attr("abs:href") ?: ""
                val overline = article.select(".teaser-cat").first()?.wholeText() ?: ""
                val author = article.select(".author-icon-text").first()?.wholeText() ?: ""
                val title = anchor.attr("title") ?: ""
                val document = getStaticContentFromUrl(url)
                val datestring = document.select(".postdate").first()?.getElementsByTag("time")?.attr("datetime") ?: ""
                val datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
                val text = document.select(".post-page").first()?.wholeText() ?: ""
                val news = NewsFactory.createNews(
                    title = title,
                    url = url,
                    provider = Tagesschau.provider,
                    overline = overline,
                    text = text,
                    dateString = datestring,
                    datePattern = datePattern,
                    author = author,
                )
                if (!news.relevant) return
                newsList.add(news)
            }
    }
}