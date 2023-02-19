package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import entityLogic.updateDisplayDate
import kotlinx.coroutines.delay
import org.jsoup.nodes.Element
import utilities.getStaticContentFromUrl

class WiWo(path: String) : AbstractScraper() {
    override val cssQuery: String = "div[data-macro=\"teaser\"]"
    override val url: String = "https://www.wiwo.de/$path"

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        val anchor = element.getElementsByTag("a").first()
        val url = anchor?.attr("abs:href") ?: ""
        val title = element.select(".js-headline").first()?.wholeOwnText() ?: ""
        val overline = element.select(".c-overline").first()?.wholeOwnText() ?: ""
        val news = NewsFactory.createNews(title = title, url = url, provider = "WiWo", overline = overline)
        val (text, dateString) = readArticle(url)
        news.text = text
        news.updateDisplayDate(dateString, "yyyy-MM-dd'T'HH:mm:ssXXX")
        if (!news.relevant) return
        newsList.add(news)
    }

    private suspend fun readArticle(url: String) = if (url.isNotEmpty()) {
        delay(delay)
        val document = getStaticContentFromUrl(url)
        val text = document.getElementsByTag("article").first()?.wholeText() ?: ""
        val datetime = document.getElementsByTag("time").attr("datetime") ?: ""
        text to datetime
    } else "" to ""
}