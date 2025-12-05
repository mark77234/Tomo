package com.markoala.tomoandroid.utils

import android.util.Log
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private const val TAG = "DateUtils"

fun parseIsoToKoreanDate(iso: String?): String {
    if (iso.isNullOrBlank()) {
        Log.w(TAG, "createdAt is null or blank")
        return ""
    }
    return try {
        Log.d(TAG, "raw createdAt: $iso")

        // "2025-11-16" 형식인 경우 직접 파싱
        if (iso.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(iso, formatter)
            val out = date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN))
            Log.d(TAG, "formatted date: $out")
            return out
        }

        // ISO 8601 전체 형식 처리
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

/**
 * 우정 기간을 "n일", "n개월", "n년" 형식으로 반환
 */
fun getFriendshipDurationText(createdAt: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startDate = LocalDate.parse(createdAt, formatter)
        val today = LocalDate.now()
        val days = ChronoUnit.DAYS.between(startDate, today) + 1 // 1일째부터 시작
        when {
            days < 30 -> "${days}일"
            days < 365 -> "${days / 30}개월"
            else -> "${days / 365}년"
        }
    } catch (e: Exception) {
        Log.e(TAG, "getFriendshipDurationText 파싱 오류: $createdAt", e)
        "-"
    }
}
