package scraper

import entities.News
import entityLogic.NewsFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class TableMedia {
    companion object {
        fun getNews(newsList: MutableList<News>) {
            Jsoup.connect("https://table.media/100-headlines/").get()
                .select(".section-presseschau")
                .forEach { parseToHeadline(it, newsList) }
        }

        private fun parseToHeadline(div: Element, newsList: MutableList<News>) {
            val divs = div.getElementsByTag("div").filter { element -> !element.hasClass("section-presseschau") }
            if (divs.size > 1) {
                divs.forEach { parseToHeadline(it, newsList) }
                println("Unexpected number of child div tags: ${divs.size}")
                return
            } else if (divs.isEmpty()) {
                System.err.println("No div tag in here")
                return
            }
            val element = divs[0]
            val childNodes = element.childNodes()
            for (i in 0 until childNodes.size step 3) {
                val text = childNodes[i]
                try {
                    val anchor = childNodes[i + 1] as Element
                    addHeadline(anchor, text.toString(), newsList)
                } catch (e: Exception) {
                } finally {
                    continue
                }
            }
        }

        private fun addHeadline(anchor: Element, text: String, newsList: MutableList<News>) {
            val url = anchor.attr("href")
            val provider = anchor.text()
            val news = NewsFactory.createNews(title = text, url = url, provider = provider)
            newsList.add(news)
        }
    }
}