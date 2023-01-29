package appLogic

import ShoppingListItem
import entities.News
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import scraper.Scraper

val collection = mutableListOf(
    ShoppingListItem("Cucumbers 🥒", 1),
    ShoppingListItem("Tomatoes 🍅", 2),
    ShoppingListItem("Orange Juice 🍊", 3)
)

fun main() {
    runBlocking {
        val port = System.getenv("PORT")?.toInt() ?: 9090
        val scraper = Scraper()
        launch(Dispatchers.IO) {
            scraper.getNews()
        }
        embeddedServer(Netty, port) {
            serverSettings()
            routings(scraper)
        }.start(wait = true)
    }
}

private fun Application.routings(scraper: Scraper) {
    routing {
        get("/") {
            call.respondText(
                this::class.java.classLoader.getResource("index.html")!!.readText(),
                ContentType.Text.Html
            )
        }
        static("/") {
            resources("")
        }
        styling()
        route(ShoppingListItem.path) {
            get {
                call.respond(collection.toList())
            }
            post {
                collection.add(call.receive<ShoppingListItem>())
                call.respond(HttpStatusCode.OK)
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                collection.removeIf { it.id == id }
                call.respond(HttpStatusCode.OK)
            }
        }
        route(News.path) {
            get { call.respond(scraper.relevantNews) }
            get("/{filterstring}") {
                call.respond(scraper.filterBy(call.parameters["filterstring"]))
            }
        }
    }
}