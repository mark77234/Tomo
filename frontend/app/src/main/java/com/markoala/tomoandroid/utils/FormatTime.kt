package com.markoala.tomoandroid.utils

fun formatTimeWithoutSeconds(time: String?): String {
    if (time.isNullOrBlank()) return "-"  // null 또는 빈값 처리

    val parts = time.split(":")
    if (parts.size < 2) return time

    val hour24 = parts[0].toIntOrNull() ?: return time
    val minute = parts[1]

    val period = if (hour24 < 12) "오전" else "오후"

    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }

    return "$period $hour12:$minute"
}
