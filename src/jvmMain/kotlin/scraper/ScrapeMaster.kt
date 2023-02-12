package scraper

import entities.News
import entityLogic.date
import entityLogic.relevant

class ScrapeMaster {
    companion object {
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
//            Nzz(),
            ManagerMagazin(),
            FinanzenNet(),
            WiWo("ticker/agentur/"),
            WiWo("ticker/"),
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
            latestNews()
        }

        private fun updateNews(newsList: MutableList<News>) {
            val oldNews = relevantNews.toSet()
            relevantNews.clear()
            relevantNews.addAll(newsList.filter { it.relevant })
            relevantNews.addAll(oldNews.filter { it.relevant })
        }

        fun filterBy(s: String?): Collection<News> {
            return if (s == null) relevantNews else relevantNews.filter { it.contains(s) }
        }
    }
}