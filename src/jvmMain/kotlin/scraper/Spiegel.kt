package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

@Suppress("BlockingMethodInNonBlockingContext")
class Spiegel : Scraper {
    companion object {
        private const val url = "https://www.spiegel.de/schlagzeilen/"

        private suspend fun parseToHeadline(div: Element, newsList: MutableList<News>) {
            delay(3000)
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
            news.text = Jsoup.connect(url).get().getElementsByTag("article").first()?.wholeText() ?: ""
            newsList.add(news)
        }
    }

    override suspend fun getNews(newsList: MutableList<News>): List<News> {
        println("Scraping: $url")
        Jsoup.connect(url).get()
            .getElementsByTag("article")
            .forEach { parseToHeadline(it, newsList) }
        return newsList
    }
}