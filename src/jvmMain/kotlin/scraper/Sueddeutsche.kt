package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.date
import entityLogic.relevant
import entityLogic.updateDisplayDate
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import utilities.printErr

class Sueddeutsche : Scraper {
    override val htmlClass: String = "entrylist__entry"
    override val tagName = ""
    override val url: String = "https://www.sueddeutsche.de/news/page/"

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getNews(newsList: MutableList<News>): List<News> {
        for (i in 1..10) {
            val url = this.url + i

            println("Scraping: $url")
            val document = Jsoup.connect(url).get()
            val select = if (htmlClass.isNotEmpty()) document.select(".$htmlClass") else document.getElementsByTag(tagName)
            select.forEach { parse(it, newsList) }
        }
        return newsList
    }

    private suspend fun parseToHeadline(div: Element, newsList: MutableList<News>) {
        delay(delay)
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
        val url = newsEntry?.getElementsByClass("entrylist__link")?.first()?.attr("href") ?: ""
        val overline = newsEntry?.getElementsByClass("entrylist__overline")?.first()?.text() ?: ""
        val title = newsEntry?.getElementsByClass("entrylist__title")?.first()?.text() ?: ""
        val author = newsEntry?.getElementsByClass("entrylist__author")?.first()?.text() ?: ""
        val teaser = newsEntry?.getElementsByClass("entrylist__detail")?.first()?.wholeOwnText() ?: ""
        val date = newsEntry?.getElementsByClass("entrylist__time")?.first()?.wholeOwnText() ?: ""
        val breadcrumbs = newsEntry?.getElementsByClass("breadcrumb-list__item")?.map { it.text() } ?: emptyList()
        val news = NewsFactory.createNews(
            title = title,
            url = url,
            provider = "Süddeutsche",
            overline = overline,
            teaser = teaser,
            breadcrumbs = breadcrumbs,
            author = author,
            displayDate = date,
            dateString = date,
//                datePattern = "yyyy-MM-dd HH:mm:ss",
            datePattern = "[dd.MM.yyyy | ][HH:]m"
        )
        if (!news.relevant){
            printErr("${news.date} $title")
            return
        }
        val (text, date2) = getArticleText(url)
        news.text = text
        news.updateDisplayDate(date2,"yyyy-MM-dd HH:mm:ss")
        newsList.add(news)
    }

    private fun getArticleText(url: String): Pair<String, String> {
        val document = Jsoup.connect(url).get()
        return (document.select("div[itemprop=\"articleBody\"]")
            .first()?.wholeText() ?: "") to document.select("time").attr("datetime")
    }

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        parseToHeadline(element, newsList)
    }
}