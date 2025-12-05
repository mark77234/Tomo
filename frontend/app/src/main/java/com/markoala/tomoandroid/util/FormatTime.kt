package com.markoala.tomoandroid.util

fun formatTimeWithoutSeconds(time: String): String {
    // Expect: "HH:mm:ss"
    val parts = time.split(":")
    if (parts.size < 2) return time  // 형식이 이상하면 원본 반환

    val hour24 = parts[0].toIntOrNull() ?: return time
    val minute = parts[1]

    // 오전/오후 계산
    val period = if (hour24 < 12) "오전" else "오후"

    // 12시간 포맷 변환
    val hour12 = when {
        hour24 == 0 -> 12          // 00 → 12 AM
        hour24 > 12 -> hour24 - 12 // 13~23 → 1~11 PM
        else -> hour24             // 1~12 그대로
    }

    return "$period $hour12:$minute"
}
