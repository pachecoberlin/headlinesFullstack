package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class WiWo : Scraper {
    override val htmlClass: String = "div[data-macro=\"teaser\"]"
    override val tagName = ""
    override val url: String = "https://www.wiwo.de/ticker/agentur/"

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        val anchor = element.getElementsByTag("a").first()
        val url = anchor?.attr("abs:href") ?: ""
        val title = element.select(".js-headline").first()?.wholeOwnText() ?: ""
        val overline = element.select(".c-overline").first()?.wholeOwnText() ?: ""
//        val dateString = element.child(0).wholeOwnText() ?: ""
//        val datePattern = "HH:mm"
        val news = NewsFactory.createNews(title = title, url = url, provider = "WiWo", overline = overline)
        if (!news.relevant) return
        @Suppress("BlockingMethodInNonBlockingContext")
        news.text = if (url.isNotEmpty()) {
            delay(1000)
            Jsoup.connect(url).get().getElementsByTag("article").first()?.wholeText() ?: ""
        } else ""
        newsList.add(news)
    }

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