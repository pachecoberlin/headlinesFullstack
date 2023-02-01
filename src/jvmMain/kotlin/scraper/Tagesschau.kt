package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class Tagesschau {
    companion object {
        private const val htmlClass = "boxCon"
        private const val baseUrl = "https://www.tagesschau.de"
        private const val url = "$baseUrl/allemeldungen/"

        fun getNews(newsList: MutableList<News>) {
            println("Scraping: $url")
            Jsoup.connect(url).get()
                .select(".$htmlClass")
                .forEach { container ->
                    container.getElementsByTag("li").forEach { li ->
                        parseToHeadline(li, newsList)
                    }
                }
        }

        private fun parseToHeadline(li: Element, newsList: MutableList<News>) {
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
            var text = document.select(".textabsatz").map { it.wholeText() }.reduce { a, b -> "$a\n$b" }
            val subtitles = document.select(".meldung__subhead").map { it.wholeText() }.reduce { a, b -> "$a\n$b" }
            text += subtitles
            val author = document.select(".authorline").first()?.wholeOwnText() ?: ""
            return text to author
        }
    }
}