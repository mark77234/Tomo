package com.markoala.tomoandroid.util

import android.util.Log
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val TAG = "DateUtils"

fun parseIsoToKoreanDate(iso: String?): String {
    if (iso.isNullOrBlank()) {
        Log.w(TAG, "createdAt is null or blank")
        return ""
    }
    return try {
        Log.d(TAG, "raw createdAt: $iso")
        val instant = Instant.parse(iso)
        Log.d(TAG, "parsed Instant (UTC): $instant")
        val zoned = instant.atZone(ZoneId.of("Asia/Seoul"))
        Log.d(TAG, "zoned (Asia/Seoul): $zoned")
        val out = zoned.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN))
        Log.d(TAG, "formatted date: $out")
        out
    } catch (e: Exception) {
        Log.e(TAG, "Failed to parse createdAt: $iso", e)
        if (iso.length >= 10 && iso[4] == '-' && iso[7] == '-') {
            val y = iso.substring(0, 4)
            val m = iso.substring(5, 7).trimStart('0')
            val d = iso.substring(8, 10).trimStart('0')
            val fallback = "${y}년 ${m}월 ${d}일"
            Log.w(TAG, "Using fallback formatted date: $fallback")
            fallback
        } else {
            iso
        }
    }
}

