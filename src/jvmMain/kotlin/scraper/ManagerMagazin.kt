package scraper

import entities.News
import entityLogic.NewsFactory
import entityLogic.relevant
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class ManagerMagazin : Scraper {
    override val htmlClass: String = ""
    override val tagName = "article"
    override val url: String = "https://www.manager-magazin.de/schlagzeilen/"

    override suspend fun parse(element: Element, newsList: MutableList<News>) {
        delay(500)
        val anchor = element.getElementsByTag("a").first()
        val url = anchor?.attr("abs:href") ?: ""
        val title = anchor?.wholeText() ?: ""
        val timeString = element.select("span[data-auxiliary]").first()?.wholeOwnText() ?: ""
//        val dateString = element.parent()?.parent()?.getElementsByTag("header")?.first()?.wholeText() ?: ""
        val datePattern = "[d. MMMM, ]HH.mm"
        val news = NewsFactory.createNews(
            title = title,
            url = url,
            provider = "Manager Magazin",
            datePattern = datePattern,
            dateString =
//            dateString+
                    timeString
        )
        if (!news.relevant) return
        @Suppress("BlockingMethodInNonBlockingContext")
        news.text = Jsoup.connect(url).get().getElementsByTag("article").first()?.wholeText() ?: ""
        newsList.add(news)
    }
}