package scraper

import entities.News
import entityLogic.*
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.format.DateTimeFormatter
import java.util.*

class WiWo : Scraper {
    override val htmlClass: String = "div[data-macro=\"teaser\"]"
    override val tagName = ""
    override val url: String = "https://www.wiwo.de/ticker/agentur/"

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        val anchor = element.getElementsByTag("a").first()
        val url = anchor?.attr("abs:href") ?: ""
        val title = element.select(".js-headline").first()?.wholeOwnText() ?: ""
        val overline = element.select(".c-overline").first()?.wholeOwnText() ?: ""
        val news = NewsFactory.createNews(title = title, url = url, provider = "WiWo", overline = overline,)
        val (text, dateString) = readArticle(url)
        news.text = text
        news.updateDisplayDate(dateString,"yyyy-MM-dd'T'HH:mm:ssXXX")
        if (!news.relevant) return
        newsList.add(news)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun readArticle(url: String) = if (url.isNotEmpty()) {
        delay(1500)
        val document = Jsoup.connect(url).get()
        val text = document.getElementsByTag("article").first()?.wholeText() ?: ""
        val datetime = document.getElementsByTag("time").attr("datetime") ?: ""
        text to datetime
    } else "" to ""

    //    TODO scraper select htmlclass and makes dot before. but that is bad for other query strings
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getNews(newsList: MutableList<News>): List<News> {
        println("Scraping: $url")
        val document = Jsoup.connect(url).get()
        val select = if (htmlClass.isNotEmpty()) document.select(htmlClass) else document.getElementsByTag(tagName)
        select.forEach { parse(it, newsList) }
        return newsList
    }
}