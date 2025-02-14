package entities

import kotlinx.serialization.Serializable

@Suppress("EqualsOrHashCode")
@Serializable
data class News(
    val title: String = "",
    val url: String = "",
    val provider: String = "",
    val overline: String = "",
    val teaser: String = "",
    var text: String = "",
    val breadcrumbs: List<String> = emptyList(),
    var author: String = "",
    var datePattern: String = "",
    var dateString: String = "",
    var source: String = "",
) {
    var displayDate: String = ""

    fun contains(searchText: String): Boolean {
        return relevantText().contains(searchText, true)
    }

    private fun relevantText(): String {
        return "$title$overline$teaser$text$breadcrumbs$author$source"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is News) {
            other.url == url
        } else {
            false
        }
    }

    val id: Int = hashCode()

    companion object {
        const val path = "/news"
    }
}