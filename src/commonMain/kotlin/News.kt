import kotlinx.serialization.Serializable

@Serializable
data class News(val title: String, val url: String, val provider: String = "", val overline: String = "", val teaser: String = "", val text: String = "") {
    val id: Int = hashCode()

    companion object {
        const val path = "/news"
    }
}