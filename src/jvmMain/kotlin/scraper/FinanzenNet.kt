package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import entityLogic.updateDisplayDate
import kotlinx.coroutines.delay
import org.jsoup.nodes.Element
import utilities.getStaticContentFromUrl

class FinanzenNet : AbstractScraper() {
    override val cssQuery: String = ".table__tbody"
    override val url: String = "https://www.finanzen.net/news/"

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        val anchor = element.getElementsByTag("a").first()
        val url = anchor?.attr("abs:href") ?: ""
        val title = anchor?.wholeText() ?: ""
        val dateString = element.child(0).wholeOwnText() ?: ""
        val datePattern = if (dateString.contains(":")) "HH:mm" else "dd.MM.yy"
        val news = NewsFactory.createNews(title = title, url = url, provider = "Finanzen.net", datePattern = datePattern, dateString = dateString)
        if (!news.relevant) return
        if (getArticleDetails) {
            delay(delay)
            val document = getStaticContentFromUrl(url)
            news.text = document.select("div[id=\"news-container\"]").first()?.wholeText() ?: ""
            document.select(".pull-left.mright-20").first()?.let {
                news.updateDisplayDate(it.wholeOwnText(), "dd.MM.yyyy HH:mm")
            }
        }
        newsList.add(news)
    }

    override suspend fun getNews(newsList: MutableList<News>): List<News> {
        println("Scraping: $url")
        val document = getStaticContentFromUrl(url)
        val select = document.select(cssQuery)
        try {
            select[1].select(".table__tr").forEach { parse(it, newsList) }
            select[3].select(".table__tr").forEach { parse(it, newsList) }
            select[4].select(".table__tr").forEach { parse(it, newsList) }
        } catch (ex: IndexOutOfBoundsException) {
            //do nothing
        }
        return newsList
    }
}