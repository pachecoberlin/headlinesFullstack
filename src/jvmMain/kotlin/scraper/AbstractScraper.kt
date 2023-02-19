package scraper

import entities.News
import utilities.getStaticContentFromUrl
import kotlin.random.Random

abstract class AbstractScraper : Scraper {
    val delay: Long
        get() = (510 + Random.nextInt(3, 300)).toLong()
    val getArticleDetails: Boolean
        get() = true

    override suspend fun getNews(newsList: MutableList<News>): List<News> {
        println("Scraping: $url")
        val document = getStaticContentFromUrl(url)
        val select = document.select(cssQuery)
        select.forEach { parse(it, newsList) }
        return newsList
    }
}
