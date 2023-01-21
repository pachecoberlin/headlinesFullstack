package entityLogic

import News
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


fun test() {
    val s1 = "freitag,20.01"
    val s2 = "vor 43 Min.".removePrefix("vor").removeSuffix("Min.").trim()
    val s = "22:53".removePrefix("Gestern").removePrefix("Heute")
    val date = DateTimeFormatter.ofPattern("eeee,dd.MM", Locale.GERMAN).parse(s)
//    val date = DateTimeFormatter.ofPattern("eeee", Locale.GERMAN).parse(s1)
    println(date)

//    val localDateTime = LocalDateTime.parse("Jul",DateTimeFormatter.ofPattern("MMM"))
//     println(localDateTime.isAfter(hour24sago))
}

//TODO Wenn nur Stunde minute und dann minus 24 stunden was passiert?
//TODO trim muss lokal passieren bei News im Konstruktor
//TODO chaching könnte auch sein
//Wenn das Jahr Fehlt dann kommt er nicht klar.
val News.date: LocalDateTime
    get() {
        return try {
            LocalDateTime.from(DateTimeFormatter.ofPattern(datePattern.replace(" ", ""), Locale.GERMAN).parse(dateString.replace(" ", "").trim()))
        } catch (e: Exception) {
            //TODO think about it good enough for sueddeutsche
            if (!dateString.isEmpty()){
                println()
            }
            LocalDateTime.now().minusMinutes(5)
        }
    }

/**
 * News are relevant when they are no older than 24 hours.
 */
val News.relevant: Boolean
    get() {
        val now = LocalDateTime.now()
        return date.isAfter(now.minusHours(24)) && date.isBefore(now)
    }

