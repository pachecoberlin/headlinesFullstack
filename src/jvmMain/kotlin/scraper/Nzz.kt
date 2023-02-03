package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class Nzz : Scraper {
    override val htmlClass: String = ""
    override val tagName = "article"
    override val url: String = "https://www.nzz.ch/neueste-artikel"

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        delay(500)
        val url = element.select(".teaser__link").attr("abs:href")
        val overline = element.select(".teaser__flag").first()?.wholeText() ?: ""
        val title = element.select(".teaser__title").first()?.wholeText() ?: ""
        val teaser = element.select(".teaser__lead").first()?.wholeText() ?: ""
        val author = element.select(".metainfo__item--author").first()?.wholeText() ?: ""
        val dateString = element.getElementsByTag("time").attr("datetime")
        val datePattern = "yyy-MM-ddTHH:mm:ss.SSSZ"
        val news = NewsFactory.createNews(title, url, "NZZ", overline, teaser, author = author, datePattern = datePattern, dateString = dateString)
        if (!news.relevant) return
        @Suppress("BlockingMethodInNonBlockingContext")
        news.text = Jsoup.connect(url).userAgent("Mozilla").get().select(".articlecomponent.text").first()?.wholeText() ?: ""
        newsList.add(news)
    }

    override suspend fun getNews(newsList: MutableList<News>): List<News> {
        //TODO javascript is loading the contents
        return newsList
    }
}