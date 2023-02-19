package scraper

import entities.News
import org.jsoup.nodes.Element

class DerStandard : AbstractScraper() {
    override val cssQuery = "article"
    override val url: String = "https://www.derstandard.at/frontpage/latest"

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        //TODO
    }

    override suspend fun getNews(newsList: MutableList<News>): List<News> {
        //TODO javascript is loading the contents
        return newsList
    }
}