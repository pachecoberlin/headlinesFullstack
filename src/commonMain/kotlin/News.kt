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
    val dateString: String = "",
    val datePattern : String = ""
) {
    fun contains(searchText: String): Boolean {
        return this.toString().contains(searchText, true)
    }

    override fun toString(): String {
        return "$title$provider$overline$teaser$text$breadcrumbs$author"
    }

    val id: Int = hashCode()

    companion object {
        const val path = "/news"
    }
}