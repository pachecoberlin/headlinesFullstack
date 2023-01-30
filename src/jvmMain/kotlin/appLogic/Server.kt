package appLogic

import ShoppingListItem
import entities.News
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.tomcat.*
import keystoreFilename
import keystorePassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import scraper.Scraper
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore


val collection = mutableListOf(
    ShoppingListItem("Cucumbers 🥒", 1),
    ShoppingListItem("Tomatoes 🍅", 2),
    ShoppingListItem("Orange Juice 🍊", 3)
)

fun main() {
    runBlocking {
        val port = System.getenv("PORT")?.toInt() ?: 9090
        launch(Dispatchers.IO) {
            Scraper.getNews()
        }
        embeddedServer(Tomcat, environment(port)).start(wait = true)
    }
}

private fun environment(port2: Int): ApplicationEngineEnvironment {
    return applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = port2
        }
        setSslConnector()
        module(Application::serverSettings)
        module(Application::routings)
    }
}

private fun ApplicationEngineEnvironmentBuilder.setSslConnector() {
    println("Setting SSL Connection")
    val (keyStoreFile, ks, pwdArray) = getKeystore()
    sslConnector(
        keyStore = ks,
        keyAlias = "www.pacheco.de",
        keyStorePassword = { pwdArray },
        privateKeyPassword = { pwdArray }
    ) {
        port = 9443
        keyStorePath = keyStoreFile
    }
}

private fun getKeystore(): Triple<File, KeyStore, CharArray> {
    val keyStoreFile = File(keystoreFilename)
    val ks = KeyStore.getInstance(KeyStore.getDefaultType())
    val pwdArray = keystorePassword.toCharArray()
    ks.load(FileInputStream(keyStoreFile), pwdArray)
    return Triple(keyStoreFile, ks, pwdArray)
}

private fun Application.routings() {
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
            get { call.respond(Scraper.relevantNews) }
            get("/{filterstring}") {
                call.respond(Scraper.filterBy(call.parameters["filterstring"]))
            }
        }
    }
}