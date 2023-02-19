package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.nodes.Element
import utilities.getStaticContentFromUrl

class Spiegel : Scraper {
    override val htmlClass: String = ""
    override val url = "https://www.spiegel.de/schlagzeilen/"
    override val tagName = "article"

    private suspend fun parseToHeadline(div: Element, newsList: MutableList<News>) {
        delay(delay)
        val anchor = div.getElementsByTag("a")
        if (anchor.size > 1) {
            anchor.forEach { parseToHeadline(it, newsList) }
            println("Unexpected number of tags: ${anchor.size}")
            return
        } else if (anchor.isEmpty()) {
            System.err.println("No anchor tag in here")
            return
        }
        val date = div.getElementsByClass("items-end").first()?.firstElementChild()?.wholeOwnText() ?: ""
        val url = anchor.attr("href")
        val title = anchor.attr("title")
        //TODO val author = they are there
        val news = NewsFactory.createNews(
            title = title,
            url = url,
            provider = "Spiegel",
            displayDate = date,
            dateString = date,
            datePattern = "[d. MMMM, ]HH.mm",
        )
        if (!news.relevant) return
        if (getArticleDetails)
            news.text = getStaticContentFromUrl(url).getElementsByTag("article").first()?.wholeText() ?: ""
        newsList.add(news)
    }


    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        parseToHeadline(element, newsList)
    }
}