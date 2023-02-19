package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.nodes.Element
import utilities.getStaticContentFromUrl

class Tonline : Scraper {
    override val tagName = ""
    override val htmlClass = "einr7x61"
    private val baseUrl = "https://www.t-online.de"
    override val url = "$baseUrl/schlagzeilen/"

    private suspend fun parseToHeadline(div: Element, newsList: MutableList<News>) {
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
        delay(delay)
        val (text, authors) = getArticleText(url)
        news.text = text
        news.author = authors
        newsList.add(news)
    }

    private suspend fun getArticleText(url: String): Pair<String, String> {
        val document = getStaticContentFromUrl(url)
        val authors = document.select("div[aria-label=\"Autoren\"]").first()?.wholeText() ?: ""
        val paragraphs = document.select(".e1i3z84t3").map { it.wholeText() }
        var text = if (paragraphs.isNotEmpty()) paragraphs.reduce { a, b -> "$a\n$b" } else ""
        val subtitlesList = document.select(".e1i8erkf9").map { it.wholeText() }
        val subtitles = if (subtitlesList.isNotEmpty()) subtitlesList.reduce { a, b -> "$a\n$b" } else ""
        text += subtitles
        return text to authors
    }

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        parseToHeadline(element, newsList)
    }
}