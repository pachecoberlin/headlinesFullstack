package scraper

import entities.News
import entityLogic.NewsFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

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
            val url = anchor.attr("href")
            val title = anchor.text()
            val time = anchor.parents()[0].wholeOwnText() ?: ""
            val date = anchor.parents()[3].firstElementChild()?.wholeOwnText() ?: ""

            newsList.add(
                NewsFactory.createNews(
                    title = title,
                    url = "$baseUrl$url",
                    provider = "Tagesschau",
                    displayDate=date,
                    dateString = time.removeSuffix(" Uhr - ") + date,
                    datePattern = "HH:mmeeee, dd. MMMM yyyy"
                )
            )
        }
    }
}