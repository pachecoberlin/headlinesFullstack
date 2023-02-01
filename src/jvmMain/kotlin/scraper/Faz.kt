package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

@Suppress("BlockingMethodInNonBlockingContext")
class Faz : Scraper {
    companion object {
        private const val htmlClass = "ticker-news-item"
        private const val url = "https://www.faz.net/faz-live"

        private suspend fun parseToHeadline(div: Element, newsList: MutableList<News>) {
            delay(3000)
            val newsContainer = div.getElementsByClass(htmlClass)
            if (newsContainer.size > 1) {
                newsContainer.forEach { parseToHeadline(it, newsList) }
                println("Unexpected number of tags: ${newsContainer.size}")
                return
            } else if (newsContainer.isEmpty()) {
                System.err.println("No tag with class:$htmlClass in here")
                return
            }

            val newsEntry = newsContainer.first()
            val titleAndLink = newsEntry?.getElementsByClass("ticker-news-title")?.first()?.getElementsByTag("a")?.first()
            val url = titleAndLink?.attr("href") ?: ""
            val overline = newsEntry?.getElementsByClass("ticker-news-super")?.first()?.wholeOwnText() ?: ""
            val title = titleAndLink?.wholeOwnText() ?: ""
            val author = newsEntry?.getElementsByClass("ticker-news-author")?.first()?.wholeOwnText() ?: ""
            val displayDate = newsEntry?.getElementsByClass("ticker-news-time")?.first()?.wholeOwnText() ?: ""
            val date = if (displayDate.length >= 16) displayDate else displayDate.substring(0..15)
            val news = NewsFactory.createNews(
                title = title,
                url = url,
                provider = "FAZ",
                overline = overline,
                author = author,
                displayDate = displayDate,
                dateString = date,
                datePattern = "dd.MM.yyyy HH:mm",
            )
            if (!news.relevant) return
            val (text, source) = getArticleText(url)
            news.text = text
            news.source = source
            newsList.add(news)
        }

        private fun getArticleText(url: String): Pair<String, String> {
            val document = Jsoup.connect(url).get()
            val text = document.select(".atc-Text").first()?.wholeText() ?: ""
            val source = document.select(".atc-Footer_Quelle").first()?.wholeOwnText()?.removePrefix("Quelle:") ?: ""
            return text to source
        }
    }

    override suspend fun getNews(newsList: MutableList<News>): List<News> {
        println("Scraping: $url")
        Jsoup.connect(url).get()
            .select(".${htmlClass}")
            .forEach { parseToHeadline(it, newsList) }
        return newsList
    }
}