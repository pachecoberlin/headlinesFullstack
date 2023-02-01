package scraper

import entities.News

interface Scraper {
    suspend fun getNews(newsList: MutableList<News>): List<News>
}
