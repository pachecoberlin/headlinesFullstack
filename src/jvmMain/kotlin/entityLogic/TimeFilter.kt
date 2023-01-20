package entityLogic

import News
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.TextStyle
import java.time.temporal.ChronoField

fun test(){
    val builder = DateTimeFormatterBuilder()
    val formatter = builder.appendLiteral("Day is:")
        .appendValue(ChronoField.DAY_OF_MONTH)
        .appendLiteral(", month is:")
        .appendValue(ChronoField.MONTH_OF_YEAR)
        .appendLiteral(", and year:")
        .appendPattern("u")
        .appendLiteral(" with the time:")
        .appendValue(ChronoField.HOUR_OF_DAY)
        .appendLiteral(":")
        .appendText(ChronoField.MINUTE_OF_HOUR, TextStyle.NARROW_STANDALONE)
        .toFormatter()
    val dateTime = LocalDateTime.now()
    val str = dateTime.format(formatter)
    val localDateTime = LocalDateTime.parse("20.01.2023 22:33 Uhr\n".substring(0..15),DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    val hour24sago = localDateTime.minusHours(23)
    println(
        hour24sago
    )
    val before = localDateTime.isBefore(LocalDateTime.now())
    println(before)

    println(localDateTime.isAfter(hour24sago))
}

val News.date: LocalDateTime
    get() {
        return try {
            LocalDateTime.from(DateTimeFormatter.ofPattern(datePattern).parse(dateString))
        } catch (e: Exception) {
            //TODO think about it
            LocalDateTime.now().minusMinutes(5)
        }
    }

/**
 * News are relevant when they are no older than 24 hours.
 */
val News.relevant: Boolean
    get() = date.isAfter(date.minusHours(24)) && date.isBefore(LocalDateTime.now())

