package entities

import kotlinx.serialization.Serializable

@Serializable
data class News(
    val title: String = "",
    val url: String = "",
    val provider: String = "",
    val overline: String = "",
    val teaser: String = "",
    val text: String = "",
    val breadcrumbs: List<String> = emptyList(),
    val author: String = "",
    val datePattern: String = ""
) {
    var displayDate: String = ""
    var dateString: String = ""

    fun contains(searchText: String): Boolean {
        return relevantText().contains(searchText, true)
    }

    fun relevantText(): String {
        return "$title$overline$teaser$text$breadcrumbs$author"
    }

    val id: Int = hashCode()

    companion object {
        const val path = "/news"
    }
}