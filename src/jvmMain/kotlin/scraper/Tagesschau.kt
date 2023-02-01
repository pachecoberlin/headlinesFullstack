package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class Tagesschau : Scraper {
    companion object {
        private const val htmlClass = "boxCon"
        private const val baseUrl = "https://www.tagesschau.de"
        private const val url = "$baseUrl/allemeldungen/"

        private suspend fun parseToHeadline(li: Element, newsList: MutableList<News>) {
            delay(3000)
            val anchor = li.getElementsByTag("a")
            val url = getURL(anchor)
            val title = anchor.text()
            val time = anchor.parents()[0].wholeOwnText() ?: ""
            val date = anchor.parents()[3].firstElementChild()?.wholeOwnText() ?: ""
            val news = NewsFactory.createNews(
                title = title,
                url = url,
                provider = "Tagesschau",
                displayDate = date,
                dateString = time + date,
                datePattern = "HH:mmeeee, dd. MMMM yyyy"
            )
            if (!news.relevant) return
            val (text, author) = getArticleText(url)
            news.text = text
            news.author = author
            newsList.add(news)
        }

        private fun getURL(anchor: Elements) = //baseUrl +
            anchor.attr("abs:href")

        private fun getArticleText(url: String): Pair<String, String> {
            val document = Jsoup.connect(url).get()
            //special case of swr.de sould be moved if swr.de scraper is implemented
            if (url.contains("www.swr.de")) return (document.getElementsByTag("article").first()?.wholeText() ?: "") to ""
            val paragraphs = document.select(".textabsatz").map { it.wholeText() }
            var text = if (paragraphs.isNotEmpty()) paragraphs.reduce { a, b -> "$a\n$b" } else ""
            val subtitlesList = document.select(".meldung__subhead").map { it.wholeText() }
            val subtitles = if (subtitlesList.isNotEmpty()) subtitlesList.reduce { a, b -> "$a\n$b" } else ""
            text += subtitles
            val author = document.select(".authorline").first()?.wholeOwnText() ?: ""
            return text to author
        }
    }

    override val htmlClass: String = Tagesschau.htmlClass
    override val tagName = ""
    override val url: String = Tagesschau.url

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        element.getElementsByTag("li").forEach { li ->
            parseToHeadline(li, newsList)
        }
    }
}