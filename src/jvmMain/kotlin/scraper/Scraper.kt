package scraper

import entities.News
import org.jsoup.nodes.Element
import utilities.getStaticContentFromUrl
import kotlin.random.Random

/**
 * TODO Interface should be an interface we need additionally an abstract class
 *
 */
interface Scraper {
    val cssQuery: String
    val url: String
    val delay: Long
        get() = (510 + Random.nextInt(3, 300)).toLong()
    val getArticleDetails: Boolean
        get() = true

    suspend fun getNews(newsList: MutableList<News>): List<News> {
        println("Scraping: $url")
        val document = getStaticContentFromUrl(url)
        val select = document.select(cssQuery)
        select.forEach { parse(it, newsList) }
        return newsList
    }

    suspend fun parse(element: Element, newsList: MutableList<News>)
}
