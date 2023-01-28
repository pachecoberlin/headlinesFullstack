package scraper

import entities.News
import entityLogic.relevant
import kotlin.streams.toList

class Scraper {
    val relevantNews = mutableListOf<News>()
    fun getNews(): List<News> {
        val newsList = mutableListOf<News>()
        Sueddeutsche.getNews(newsList)
        Faz.getNews(newsList)
        Tagesschau.getNews(newsList)
        Zdf.getNews(newsList)
        Spiegel.getNews(newsList)
        Tonline.getNews(newsList)
        TableMedia.getNews(newsList)
        relevantNews.addAll(newsList.parallelStream().filter { it.relevant }.toList())
        return relevantNews
    }

    fun filterBy(s: String?): List<News> {
        return if (s == null) relevantNews else relevantNews.filter { it.contains(s) }
    }
}