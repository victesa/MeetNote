package com.victorkirui.meetnote.domain.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

object DateUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRelativeTimeDistance(
        timestamp: Long?
    ): String {
        if (timestamp == null) return "Never"
        
        return try {
            val inputDate = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val currentDate = LocalDate.now()

            calculateDistance(inputDate, currentDate)
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRelativeTimeDistance(
        dateString: String,
        pattern: String = "yyyy-MM-dd"
    ): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            val inputDate = LocalDate.parse(dateString, formatter)
            val currentDate = LocalDate.now()

            calculateDistance(inputDate, currentDate)
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDistance(inputDate: LocalDate, currentDate: LocalDate): String {
        val days = ChronoUnit.DAYS.between(inputDate, currentDate)
        val weeks = ChronoUnit.WEEKS.between(inputDate, currentDate)
        val months = ChronoUnit.MONTHS.between(inputDate, currentDate)
        val years = ChronoUnit.YEARS.between(inputDate, currentDate)

        val isPast = days >= 0
        val absDays = abs(days)
        val absWeeks = abs(weeks)
        val absMonths = abs(months)
        val absYears = abs(years)

        return when {
            absDays == 0L -> "Today"
            absYears > 0L -> buildString(absYears, "year", isPast)
            absMonths > 0L -> buildString(absMonths, "month", isPast)
            absWeeks > 0L -> buildString(absWeeks, "week", isPast)
            else -> buildString(absDays, "day", isPast)
        }
    }

    private fun buildString(value: Long, unit: String, isPast: Boolean): String {
        val pluralS = if (value == 1L) "" else "s"
        return if (isPast) {
            "$value $unit$pluralS ago"
        } else {
            "In $value $unit$pluralS"
        }
    }
}
