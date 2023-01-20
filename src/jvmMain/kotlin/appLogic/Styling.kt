package appLogic

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration

internal fun Routing.styling() {
    get("/styles.css") {
        call.respondCss {
            body {
                backgroundColor = Color.lightGray
                margin(10.px)
            }
            rule(".page_title") {
                color = Color.gray
                margin(all = LinearDimension.auto)
                width = LinearDimension("50%")
            }
            rule("a") {
                color = Color.black
                backgroundColor = Color.transparent
                textDecoration = TextDecoration.none
            }
            rule("li") {
                padding(5.px)
            }
        }
    }
}

internal suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}