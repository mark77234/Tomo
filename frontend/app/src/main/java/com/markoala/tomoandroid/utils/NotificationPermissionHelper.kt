package com.markoala.tomoandroid.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationPermissionHelper {

    // 알림 권한 요청 팝업을 이미 본 적 있는지 저장하는 로컬 저장소(Prefs)
    private const val PREFS_NAME = "notification_prefs"
    private const val KEY_PERMISSION_REQUESTED = "post_notifications_requested"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun hasRequestedPermission(context: Context): Boolean { // 알림 권한 요청한 적이 있는지 여부 반환
        return prefs(context).getBoolean(KEY_PERMISSION_REQUESTED, false)
    }

    fun markPermissionRequested(context: Context) { // 앱 권한 요청한 사실을 저장 (앱이 최초로 다이얼로그를 띄운 시점에 호출)
        prefs(context).edit().putBoolean(KEY_PERMISSION_REQUESTED, true).apply()
    }

    fun resetPermissionRequested(context: Context) { // 저장된 요청 여부 초기화
        prefs(context).edit().putBoolean(KEY_PERMISSION_REQUESTED, false).apply()
    }

    fun isPermissionGranted(context: Context): Boolean { // 앱 내에서 알림 권한이 실제로 부여되었는지 여부 반환
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun areNotificationsEnabled(context: Context): Boolean { // OS 설정에서 알림이 허용되어 있는지 여부 반환
        val notificationsAllowed = NotificationManagerCompat.from(context).areNotificationsEnabled()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationsAllowed && isPermissionGranted(context)
        } else {
            notificationsAllowed
        }
    }

    fun shouldShowInitialPrompt(context: Context): Boolean { // 최초 알림 권한 요청 팝업을 띄워야 하는지 여부 반환
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !hasRequestedPermission(context) &&
            !isPermissionGranted(context)
    }

    fun openNotificationSettings(context: Context) { // 앱의 알림 설정 화면 열기
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }
}
