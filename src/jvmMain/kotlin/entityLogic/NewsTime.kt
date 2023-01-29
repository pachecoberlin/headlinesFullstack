package entityLogic

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import java.util.*

class NewsTime {
    companion object {
        val fallBackDateTime: LocalDateTime = LocalDateTime.now().minusMinutes(5)
        internal fun createLocalDateTime(datePattern: String, dateString: String): LocalDateTime {
            //TODO think about the fallbacktime, it's good enough for sueddeutsche
            val pattern = try {
                DateTimeFormatter.ofPattern(datePattern, Locale.GERMAN)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                return fallBackDateTime
            }
            val temporalAccessor = pattern.parse(dateString)
            return LocalDateTime.of(createLocalDate(temporalAccessor), createLocalTime(temporalAccessor))
        }

        private fun createLocalTime(temporalAccessor: TemporalAccessor): LocalTime {
            val now = LocalTime.now()
            val hour = if (temporalAccessor.isSupported(ChronoField.HOUR_OF_DAY)) temporalAccessor.get(ChronoField.HOUR_OF_DAY) else now.hour
            val minute = if (temporalAccessor.isSupported(ChronoField.MINUTE_OF_HOUR)) temporalAccessor.get(ChronoField.MINUTE_OF_HOUR) else now.minute
            return LocalTime.of(hour, minute)
        }

        private fun createLocalDate(temporalAccessor: TemporalAccessor): LocalDate {
            val now = LocalDate.now()
            val year = if (temporalAccessor.isSupported(ChronoField.YEAR)) temporalAccessor.get(ChronoField.YEAR) else now.year
            val month = if (temporalAccessor.isSupported(ChronoField.MONTH_OF_YEAR)) temporalAccessor.get(ChronoField.MONTH_OF_YEAR) else now.monthValue
            val day = if (temporalAccessor.isSupported(ChronoField.DAY_OF_MONTH)) temporalAccessor.get(ChronoField.DAY_OF_MONTH) else now.dayOfMonth
            return LocalDate.of(year, month, day)
        }
    }
}