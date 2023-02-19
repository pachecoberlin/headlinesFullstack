package scraper

import entities.News
import org.jsoup.nodes.Element
import utilities.getStaticContentFromUrl
import kotlin.random.Random

/**
 * TODO Only htmlClass or tagName should be used. Anyway htmlClass is used before tagName. we only need a cssQuery which is enough to select classes and or tags. Point needs to be removed before use of htmlClass.
 * TODO Interface should be an interface we need additonally an abstract class
 *
 */
interface Scraper {
    val htmlClass: String
    val tagName: String
    val url: String
    val delay: Long
        get() = (510 + Random.nextInt(3, 300)).toLong()
    val getArticleDetails: Boolean
        get() = false

    suspend fun getNews(newsList: MutableList<News>): List<News> {
        println("Scraping: $url")
        val document = getStaticContentFromUrl(url)
        val select = if (htmlClass.isNotEmpty()) document.select(".$htmlClass") else document.getElementsByTag(tagName)
        select.forEach { parse(it, newsList) }
        return newsList
    }

    suspend fun parse(element: Element, newsList: MutableList<News>)
}
