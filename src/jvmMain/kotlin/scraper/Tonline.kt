package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class Tonline : Scraper {
    companion object {
        private const val htmlClass = "einr7x61"
        private const val baseUrl = "https://www.t-online.de"
        private const val url = "$baseUrl/schlagzeilen/"

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
            val titleAndLink = newsEntry?.getElementsByTag("a")?.first()
            val url = titleAndLink?.attr("abs:href") ?: ""
            val title = titleAndLink?.wholeOwnText() ?: ""
            val overline = newsEntry?.getElementsByClass("css-169b1y4")?.first()?.text() ?: ""
            val date = div.parent()?.parent()?.parent()?.getElementsByClass("eamwxa70")?.first()?.wholeOwnText() ?: ""
            val news = NewsFactory.createNews(
                title = title,
                url = url,
                provider = "T-Online",
                overline = overline,
                displayDate = date,
                dateString = date,
                datePattern = "eeee, dd.MM",
            )
            if (!news.relevant) return
            val (text, authors) = getArticleText(url)
            news.text = text
            news.author = authors
            newsList.add(news)
        }

        private fun getArticleText(url: String): Pair<String, String> {
            val document = Jsoup.connect(url).get()
            val authors = document.select("div[aria-label=\"Autoren\"]").first()?.wholeText() ?: ""
            val paragraphs = document.select(".e1i3z84t3").map { it.wholeText() }
            var text = if (paragraphs.isNotEmpty()) paragraphs.reduce { a, b -> "$a\n$b" } else ""
            val subtitlesList = document.select(".e1i8erkf9").map { it.wholeText() }
            val subtitles = if (subtitlesList.isNotEmpty()) subtitlesList.reduce { a, b -> "$a\n$b" } else ""
            text += subtitles
            return text to authors
        }
    }

    override val htmlClass: String = Tonline.htmlClass
    override val tagName = ""
    override val url: String = Tonline.url

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        parseToHeadline(element, newsList)
    }
}