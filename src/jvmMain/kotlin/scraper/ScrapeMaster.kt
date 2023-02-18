package scraper

import entities.News
import entityLogic.date
import entityLogic.relevant
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class ScrapeMaster {
    companion object {
        private val newsUpdater = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val relevantNews = mutableSetOf<News>().toSortedSet { news1, news2 -> news2.date.compareTo(news1.date) }
        private val scrapers = listOf(
            Sueddeutsche(),
            Faz(),
            Tagesschau(),
            TagesschauWirtschaft(),
            Zdf(),
            ZdfWirtschaft(),
            Spiegel(),
            Tonline(),
            ManagerMagazin(),
            FinanzenNet(),
            WiWo("ticker/agentur/"),
            WiWo("ticker/"),
//            DerStandard(),
//            Nzz(),
        )
        val scraperDispatchers = scrapers.map { it to Executors.newSingleThreadExecutor().asCoroutineDispatcher() }

        suspend fun getNews(): Collection<News> {
            latestNews()
            return relevantNews
        }

        private suspend fun latestNews() {
            coroutineScope {
                scraperDispatchers.forEach { (scraper, dispatcher) ->
                    launch(dispatcher) {
                        val newsList = mutableListOf<News>()
                        try {
                            scraper.getNews(newsList)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        } finally {
                            updateNews(newsList)
                        }
                    }
                }
            }
            latestNews()
        }

        private suspend fun updateNews(newsList: MutableList<News>) {
            coroutineScope {
                launch(newsUpdater) {
                    val oldNews = relevantNews.toSet()
                    relevantNews.clear()
                    relevantNews.addAll(newsList.filter { it.relevant })
                    relevantNews.addAll(oldNews.filter { it.relevant })
                }
            }
        }

        fun filterBy(s: String?): Collection<News> {
            return if (s == null) relevantNews else relevantNews.filter { it.contains(s) }
        }
    }
}