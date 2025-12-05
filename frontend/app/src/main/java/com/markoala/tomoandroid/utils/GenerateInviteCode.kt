package com.markoala.tomoandroid.utils

fun generateInviteCode(userId: String): String {
    return if (userId.isNotBlank() && userId.length >= 4) {
        "TOMO-${userId.takeLast(4)}"
    } else {
        "TOMO-0000"
    }
}
