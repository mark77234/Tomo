package com.markoala.tomoandroid.data.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.markoala.tomoandroid.MainActivity
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.utils.NotificationPermissionHelper

class TomoFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        // 토큰이 새로 생성되었거나 갱신되었을 때 서버에 전송
        // 예: AuthManager.sendFcmTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // 데이터 페이로드가 있는 경우
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            // 앱 로직에 맞게 처리 (예: 데이터 기반 내부 동작)
        }

        // 알림 페이로드가 있는 경우
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Title: ${it.title}")
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title ?: "", it.body ?: "")
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        if (!NotificationPermissionHelper.areNotificationsEnabled(this)) {
            Log.d(TAG, "Notifications are disabled. Skipping push message.")
            return
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.ic_tomo)  // manifest에 설정한 기본 아이콘
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(/* notification id */ 0, notificationBuilder.build())
    }
}
