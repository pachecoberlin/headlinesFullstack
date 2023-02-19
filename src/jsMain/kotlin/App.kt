import csstype.ClassName
import entities.News
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.FC
import react.Props
import react.dom.html.AnchorTarget
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.em
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.strong
import react.useEffectOnce
import react.useState

private val scope = MainScope()

val App = FC<Props> { _ ->
    var news by useState(emptyList<News>())

    useEffectOnce {
        scope.launch {
            news = getNews()
        }
    }
    div {
        className = ClassName("page_title")
        h1 {
            +"Sören's fast Scraper"
        }
        InputComponent {
            onSubmit = { input ->
                scope.launch {
                    news = if (input.isEmpty()) getNews() else filterResults(input)
                }
            }
        }
        p {
            +"Searching through ${news.size} articles from the last 24 hours"
        }
    }
    div {
        id = "headLineListContainer"
        ol {
            id = "headLineList"
            news.forEach { item ->
                li {
                    a {
                        if (item.url.isNotEmpty()) href = item.url
                        target = AnchorTarget._blank
                        div {
                            +item.displayDate
                        }
                        div {
                            +item.provider
                        }
                        div {
                            strong { +item.overline }
                        }
                        div {
                            em {
                                +item.title
                            }
                        }
                        div {
                            +item.teaser
                        }
//                        div {
//                            +item.text
//                        }
                    }
                }
            }
        }
    }
}
