package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class Sueddeutsche {
    companion object {
        private const val htmlClass = "entrylist__entry"
        private const val url = "https://www.sueddeutsche.de/news"

        fun getNews(newsList: MutableList<News>) {
            println("Scraping: $url")
            Jsoup.connect(url).get()
                .select(".$htmlClass")
                .forEach { parseToHeadline(it, newsList) }
        }

        private fun parseToHeadline(div: Element, newsList: MutableList<News>) {
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
            if (!news.relevant) return
            val (text, date2) = getArticleText(url)
            news.text = text
            news.displayDate = date2
            news.dateString = date2
            news.datePattern = "yyyy-MM-dd HH:mm:ss"
            newsList.add(news)
        }

        private fun getArticleText(url: String): Pair<String, String> {
            val document = Jsoup.connect(url).get()
            return (document.select("div[itemprop=\"articleBody\"]")
                .first()?.wholeText() ?: "") to document.select("time").attr("datetime")
        }
    }
}