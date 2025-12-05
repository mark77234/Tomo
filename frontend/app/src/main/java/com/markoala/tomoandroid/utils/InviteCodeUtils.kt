package com.markoala.tomoandroid.util

import android.content.Context
import android.content.Intent

fun shareInviteCode(context: Context, inviteCode: String) {
    val deepLink = "tomoapp://invite/$inviteCode"
    val shareText = "Tomo 앱에 초대합니다! 🎉\n초대 코드: $inviteCode\n\n초대하러 가기: $deepLink"

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "초대 코드 공유")
    context.startActivity(shareIntent)
}

