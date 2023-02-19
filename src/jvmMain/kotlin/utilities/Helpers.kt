package utilities

import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun printErr(errorMsg: Any) {
    System.err.println(errorMsg)
}

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun getStaticContentFromUrl(url: String): Document {
    if (url.isEmpty()) return Document("url")
    return try {
        Jsoup.connect(url).get()
    } catch (ex: Exception) {
        delay(5000)
        try {
            Jsoup.connect(url).get()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Document(url.ifEmpty { "url" })
        }
    }
}