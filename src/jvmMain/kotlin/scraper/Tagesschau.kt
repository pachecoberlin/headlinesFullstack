package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import scraper.Tagesschau.Companion.provider
import utilities.getStaticContentFromUrl

class Tagesschau : AbstractScraper() {
    companion object {
        val provider = "Tagesschau"
    }

    override val cssQuery = ".boxCon"
    private val baseUrl = "https://www.tagesschau.de"
    override val url = "$baseUrl/allemeldungen/"

    private suspend fun parseToHeadline(li: Element, newsList: MutableList<News>) {
        val anchor = li.getElementsByTag("a")
        val url = getURL(anchor)
        val title = anchor.text()
        val time = anchor.parents()[0].wholeOwnText() ?: ""
        val date = anchor.parents()[3].firstElementChild()?.wholeOwnText() ?: ""
        val news = NewsFactory.createNews(
            title = title,
            url = url,
            provider = provider,
            displayDate = date,
            dateString = time + date,
            datePattern = "HH:mmeeee, dd. MMMM yyyy"
        )
        if (!news.relevant) return
        if (getArticleDetails) {
            delay(delay)
            val (text, author) = getArticleText(url)
            news.text = text
            news.author = author
        }
        newsList.add(news)
    }

    private fun getURL(anchor: Elements) = //baseUrl +
        anchor.attr("abs:href")

    private suspend fun getArticleText(url: String): Pair<String, String> {
        val document = getStaticContentFromUrl(url)
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


    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        element.getElementsByTag("li").forEach { li ->
            parseToHeadline(li, newsList)
        }
    }
}

class TagesschauWirtschaft : AbstractScraper() {
    override val cssQuery: String = ".teaser__link"
    override val url: String = "https://www.tagesschau.de/wirtschaft/"

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        delay(delay)
        val url = element.attr("abs:href") ?: ""
        val overline = element.select(".teaser__topline").first()?.wholeOwnText() ?: ""
        val title = element.select(".teaser__headline").first()?.wholeOwnText() ?: ""
        val teaser = element.select(".teaser__shorttext").first()?.wholeText() ?: ""
        val document = getStaticContentFromUrl(url)
        val datestring = document.select(".metatextline").first()?.wholeOwnText() ?: ""
        val datePattern = "dd.MM.yyyy HH:mm"
        val text = document.getElementsByTag("main").first()?.wholeText() ?: ""
        val news = NewsFactory.createNews(
            title = title,
            url = url,
            provider = provider,
            overline = overline,
            teaser = teaser,
            text = text,
            dateString = datestring,
            datePattern = datePattern
        )
        if (!news.relevant) return
        newsList.add(news)
    }
}