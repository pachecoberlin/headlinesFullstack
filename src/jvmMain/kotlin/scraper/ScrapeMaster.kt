package scraper

import entities.News
import entityLogic.relevant
import kotlinx.coroutines.delay
import kotlin.streams.toList

class ScrapeMaster {
    companion object {
        val relevantNews = mutableSetOf<News>()
        private val scrapers = listOf(
            Sueddeutsche(),
            Faz(),
            Tagesschau(),
            Zdf(),
            Spiegel(),
            Tonline()
        )
        suspend fun getNews(): Collection<News> {
            latestNews()
            return relevantNews
        }

        private suspend fun latestNews() {
            val newsList = mutableListOf<News>()
            scrapers.forEach {
                try {
                    it.getNews(newsList)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                } finally {
                    updateNews(newsList)
                }
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