package com.markoala.tomoandroid.data.fcm

import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.markoala.tomoandroid.BuildConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

object FcmPushSender {
    private const val TAG = "FcmPushSender"
    private val client by lazy { OkHttpClient() }
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val messagingScopes = listOf("https://www.googleapis.com/auth/firebase.messaging")
    private val cachedCredentials = AtomicReference<GoogleCredentials?>()
    private val backgroundExecutor by lazy { Executors.newSingleThreadExecutor() }

    fun sendNotification(
        targetToken: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ) {
        backgroundExecutor.execute {
            sendNotificationInternal(targetToken, title, body, data)
        }
    }

    private fun sendNotificationInternal(
        targetToken: String,
        title: String,
        body: String,
        data: Map<String, String>
    ) {
        val projectId = BuildConfig.FCM_PROJECT_ID.takeIf { it.isNotBlank() }
        if (projectId == null) {
            Log.w(TAG, "FCM projectId가 설정되지 않아 푸시 알림을 건너뜁니다.")
            return
        }

        val credentials = getCredentials() ?: return
        val accessToken = fetchAccessToken(credentials) ?: return

        val payload = JSONObject().apply {
            put(
                "message",
                JSONObject().apply {
                    put("token", targetToken)
                    put(
                        "notification",
                        JSONObject().apply {
                            put("title", title)
                            put("body", body)
                        }
                    )
                    if (data.isNotEmpty()) {
                        put("data", JSONObject(data))
                    }
                }
            )
        }

        val request = Request.Builder()
            .url("https://fcm.googleapis.com/v1/projects/$projectId/messages:send")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "푸시 알림 발송 실패", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.w(TAG, "푸시 알림 발송 응답 실패: ${it.code} ${it.message}")
                    } else {
                        Log.d(TAG, "푸시 알림 발송 성공")
                    }
                }
            }
        })
    }

    private fun getCredentials(): GoogleCredentials? {
        cachedCredentials.get()?.let { return it }

        val clientEmail = BuildConfig.FCM_CLIENT_EMAIL
        val privateKey = BuildConfig.FCM_PRIVATE_KEY

        if (clientEmail.isBlank() || privateKey.isBlank()) {
            Log.w(TAG, "FCM 서비스 계정 정보가 누락되어 푸시 알림을 건너뜁니다.")
            return null
        }

        val serviceAccountJson = """
            {
              "type": "service_account",
              "project_id": "${BuildConfig.FCM_PROJECT_ID}",
              "private_key_id": "${BuildConfig.FCM_PRIVATE_KEY_ID}",
              "private_key": "${privateKey.replace("\\n", "\n")}",
              "client_email": "$clientEmail",
              "client_id": "${BuildConfig.FCM_CLIENT_ID}",
              "token_uri": "https://oauth2.googleapis.com/token"
            }
        """.trimIndent()

        return try {
            val credentials = GoogleCredentials
                .fromStream(ByteArrayInputStream(serviceAccountJson.toByteArray()))
                .createScoped(messagingScopes)
            cachedCredentials.set(credentials)
            credentials
        } catch (e: Exception) {
            Log.e(TAG, "서비스 계정 크리덴셜 생성 실패", e)
            null
        }
    }

    private fun fetchAccessToken(credentials: GoogleCredentials): String? =
        try {
            credentials.refreshIfExpired()
            credentials.accessToken?.tokenValue ?: credentials.refreshAccessToken().tokenValue
        } catch (e: Exception) {
            Log.e(TAG, "FCM 액세스 토큰 발급 실패", e)
            null
        }
}
