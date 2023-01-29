package scraper

import entities.News
import entityLogic.relevant
import kotlinx.coroutines.delay
import kotlin.streams.toList

class Scraper {
    val relevantNews = mutableSetOf<News>()
    suspend fun getNews(): Collection<News> {
        latestNews()
        return relevantNews
    }

    private suspend fun latestNews() {
        val newsList = mutableListOf<News>()
        Sueddeutsche.getNews(newsList)
        Faz.getNews(newsList)
        Tagesschau.getNews(newsList)
        Zdf.getNews(newsList)
        Spiegel.getNews(newsList)
        Tonline.getNews(newsList)
        TableMedia.getNews(newsList)
        relevantNews.addAll(newsList.parallelStream().filter { it.relevant }.toList())
        delay(1_800_000)
        latestNews()
    }

    fun filterBy(s: String?): Collection<News> {
        return if (s == null) relevantNews else relevantNews.filter { it.contains(s) }
    }
}