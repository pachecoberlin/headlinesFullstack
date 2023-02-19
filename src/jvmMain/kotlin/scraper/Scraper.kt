package scraper

import entities.News
import org.jsoup.nodes.Element

interface Scraper {
    val cssQuery: String
    val url: String

    suspend fun getNews(newsList: MutableList<News>): List<News>

    suspend fun parse(element: Element, newsList: MutableList<News>)
}
