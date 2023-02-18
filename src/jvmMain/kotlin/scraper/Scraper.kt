package scraper

import entities.News
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import kotlin.random.Random

/**
 * Only htmlClass or tagName should be used. Anyway htmlClass is used before tagName.
 */
interface Scraper {
    val htmlClass: String
    val tagName: String
    val url: String
    val delay: Long
        get() = (450 + Random.nextInt(3, 300)).toLong()

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getNews(newsList: MutableList<News>): List<News> {
        println("Scraping: $url")
        val document = Jsoup.connect(url).get()
        val select = if (htmlClass.isNotEmpty()) document.select(".$htmlClass") else document.getElementsByTag(tagName)
        select.forEach { parse(it, newsList) }
        return newsList
    }

    suspend fun parse(element: Element, newsList: MutableList<News>)
}
