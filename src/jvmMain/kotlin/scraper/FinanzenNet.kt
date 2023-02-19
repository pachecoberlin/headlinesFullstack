package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import entityLogic.updateDisplayDate
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class FinanzenNet : Scraper {
    override val htmlClass: String = "table__tbody"
    override val tagName = ""
    override val url: String = "https://www.finanzen.net/news/"

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        val anchor = element.getElementsByTag("a").first()
        val url = anchor?.attr("abs:href") ?: ""
        val title = anchor?.wholeText() ?: ""
        val dateString = element.child(0).wholeOwnText() ?: ""
        val datePattern = if (dateString.contains(":")) "HH:mm" else "dd.MM.yy"
        val news = NewsFactory.createNews(title = title, url = url, provider = "Finanzen.net", datePattern = datePattern, dateString = dateString)
        if (!news.relevant) return
        @Suppress("BlockingMethodInNonBlockingContext")
        if (url.isNotEmpty()) {
            delay(delay)
            val document = Jsoup.connect(url).get()
            news.text = document.select("div[id=\"news-container\"]").first()?.wholeText() ?: ""
            document.select(".pull-left.mright-20").first()?.let {
                news.updateDisplayDate(it.wholeOwnText(), "dd.MM.yyyy HH:mm")
            }
            news.dateString = document.select(".pull-left.mright-20").first()?.wholeOwnText() ?: news.displayDate
        }
        newsList.add(news)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getNews(newsList: MutableList<News>): List<News> {
        println("Scraping: $url")
        val document = Jsoup.connect(url).get()
        val select = document.select(".$htmlClass")
        try {
            select[1].select(".table__tr").forEach { parse(it, newsList) }
            select[3].select(".table__tr").forEach { parse(it, newsList) }
            select[4].select(".table__tr").forEach { parse(it, newsList) }
        } catch (ex: IndexOutOfBoundsException) {
            //do nothing}
        }
        return newsList
    }
}