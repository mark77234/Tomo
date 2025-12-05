package com.markoala.tomoandroid.ui.main.calendar.model

import java.time.LocalDate

enum class CalendarEventType { MOIM, PROMISE }

data class CalendarEvent(
    val id: String,
    val date: LocalDate,
    val title: String,
    val description: String? = null,
    val type: CalendarEventType,
    val moimId: Int? = null,
    val promiseTime: String? = null,
    val place: String? = null,
    val moimTitle: String? = null
)
