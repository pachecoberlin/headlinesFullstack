package scraper

import entities.News

interface Scraper {
    fun getNews(newsList: MutableList<News>): List<News>
}
