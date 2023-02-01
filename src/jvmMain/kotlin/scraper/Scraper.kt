package scraper

import entities.News
import entityLogic.relevant
import kotlinx.coroutines.delay
import kotlin.streams.toList

class Scraper {
    companion object {
        val relevantNews = mutableSetOf<News>()
        suspend fun getNews(): Collection<News> {
            latestNews()
            return relevantNews
        }

        private suspend fun latestNews() {
            try {//TODO try and catch for each and also implement interface for scrapers
                val newsList = mutableListOf<News>()
                Sueddeutsche.getNews(newsList)
                Faz.getNews(newsList)
                Tagesschau.getNews(newsList)
                Zdf.getNews(newsList)
                Spiegel.getNews(newsList)
                Tonline.getNews(newsList)
                TableMedia.getNews(newsList)
                updateNews(newsList)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            delay(1_800_000)
            latestNews()
        }

        private fun updateNews(newsList: MutableList<News>) {
            val oldNews = relevantNews.toSet()
            relevantNews.clear()
            relevantNews.addAll(newsList.stream().filter { it.relevant }.toList())
            relevantNews.addAll(oldNews)
        }

        fun filterBy(s: String?): Collection<News> {
            return if (s == null) relevantNews else relevantNews.filter { it.contains(s) }
        }
    }
}