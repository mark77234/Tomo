package com.markoala.tomoandroid.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

fun generateCalendarMatrix(month: YearMonth): List<List<LocalDate>> {
    val first = month.atDay(1)
    val index = when (first.dayOfWeek) {
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 0
    }
    val startDate = first.minusDays(index.toLong())
    return (0 until 42).map { startDate.plusDays(it.toLong()) }.chunked(7)
}
