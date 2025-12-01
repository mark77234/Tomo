package com.markoala.tomoandroid.data.repository.friends

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.markoala.tomoandroid.data.fcm.FcmPushSender
import com.markoala.tomoandroid.data.api.friendsApi

import com.markoala.tomoandroid.data.model.friends.FriendSummary
import com.markoala.tomoandroid.data.model.user.BaseResponse
import com.markoala.tomoandroid.data.repository.AuthRepository
import com.markoala.tomoandroid.utils.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // 친구 검색 함수 (GET)
    fun getFriends(
        email: String,
        onLoading: (Boolean) -> Unit,
        onSuccess: (List<FriendSummary>) -> Unit,
        onError: (statusCode: Int?, message: String) -> Unit
    ) {
        Log.d("FriendsRepository", "getFriends 시작 - 입력된 이메일: $email")

        if (email.isBlank()) {
            Log.w("FriendsRepository", "이메일이 비어있음")

            return
        }

        onLoading(true)
        Log.d("FriendsRepository", "검색 상태 변경: loading = true")

        // AuthInterceptor가 자동으로 토큰을 추가하므로 직접 API 호출
        Log.d("FriendsRepository", "getFriends API 요청 생성")

        val call = friendsApi.getFriends(email)
        Log.d("FriendsRepository", "getFriends API 호출 시작")

        call.enqueue(object : Callback<BaseResponse<FriendSummary>> {
            override fun onResponse(
                call: Call<BaseResponse<FriendSummary>>,
                response: Response<BaseResponse<FriendSummary>>
            ) {
                Log.d("FriendsRepository", "getFriends API 응답 수신")
                Log.d("FriendsRepository", "응답 코드: ${response.code()}")

                onLoading(false)
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("FriendsRepository", "응답 본문: $result")

                    if (result?.success == true) {
                        Log.d(
                            "FriendsRepository",
                            "친구 검색 성공 - 찾은 친구: ${result.data.username}"
                        )
                        onSuccess(listOf(result.data)) // 단일 객체를 리스트로 변환
                    } else {
                        Log.w("FriendsRepository", "친구 검색 실패")
                        onError(response.code(), result?.message ?: "친구를 찾을 수 없습니다")
                    }
                } else {
                    Log.e("FriendsRepository", "HTTP 응답 실패 - 코드: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("FriendsRepository", "에러 본문: $errorBody")

                    // HTTP 상태 코드에 따른 구체적인 에러 메시지 생성
                    val errorResult = ErrorHandler.handleHttpError(response.code(), errorBody)
                    onError(response.code(), errorResult.message)
                }
            }

            override fun onFailure(call: Call<BaseResponse<FriendSummary>>, t: Throwable) {
                Log.e("FriendsRepository", "getFriends API 요청 실패", t)
                onLoading(false)
                onError(null, "네트워크 오류가 발생했습니다")
            }
        })
    }

    // 친구 추가 함수 (POST)
    fun postFriends(
        email: String,
        onLoading: (Boolean) -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("FriendsRepository", "postFriends 시작 - 입력된 이메일: $email")

        if (email.isBlank()) {
            Log.w("FriendsRepository", "이메일이 비어있음")

            return
        }

        onLoading(true)
        Log.d("FriendsRepository", "친구 추가 상태 변경: loading = true")


        Log.d("FriendsRepository", "API 요청 생성 - 요청 데이터: $email")

        val call = friendsApi.postFriends(email)
        Log.d("FriendsRepository", "API 호출 시작 - URL: ${call.request().url}")
        Log.d("FriendsRepository", "HTTP 메소드: ${call.request().method}")

        call.enqueue(object : Callback<BaseResponse<FriendSummary?>> {
            override fun onResponse(
                call: Call<BaseResponse<FriendSummary?>>,
                response: Response<BaseResponse<FriendSummary?>>
            ) {
                Log.d("FriendsRepository", "API 응답 수신")
                Log.d("FriendsRepository", "응답 코드: ${response.code()}")
                Log.d("FriendsRepository", "응답 메시지: ${response.message()}")
                Log.d("FriendsRepository", "응답 성공 여부: ${response.isSuccessful}")

                onLoading(false)
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("FriendsRepository", "응답 본문: $result")

                    if (result?.success == true) {
                        Log.d(
                            "FriendsRepository",
                            "친구 추가 성공 - message: ${result.message}"
                        )

                        notifyFriendAdded(result.data, email)
                        onSuccess()
                    } else {
                        Log.w(
                            "FriendsRepository",
                            "친구 추가 실패 - success: ${result?.success}, data: ${result?.data}, message: ${result?.message}"
                        )
                        val errorMsg = result?.message ?: "친구 추가에 실패했습니다"
                        onError(errorMsg)

                    }
                } else {
                    Log.e(
                        "FriendsRepository",
                        "HTTP 응답 실패 - 코드: ${response.code()}, 메시지: ${response.message()}"
                    )
                    val errorBody = response.errorBody()?.string()
                    Log.e("FriendsRepository", "에러 본문: $errorBody")

                    // HTTP 상태 코드에 따른 구체적인 에러 메시지 생성
                    val errorResult = ErrorHandler.handleHttpError(response.code(), errorBody)
                    onError(errorResult.message)

                }
            }

            override fun onFailure(call: Call<BaseResponse<FriendSummary?>>, t: Throwable) {
                Log.e("FriendsRepository", "API 요청 완전 실패", t)
                Log.e("FriendsRepository", "실패 원인: ${t.javaClass.simpleName}")
                Log.e("FriendsRepository", "에러 메시지: ${t.message}")
                Log.e("FriendsRepository", "요청 URL: ${call.request().url}")

                onLoading(false)
                onError("네트워크 오류")

            }
        })
    }

    private fun fetchFriendFcmTokenByEmail(
        email: String,
        onFoundToken: (String) -> Unit,
        onMissing: (() -> Unit)? = null
    ) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshots ->
                val doc = snapshots.documents.firstOrNull()
                val fcmToken = doc?.getString("fcmToken")
                val targetUid = doc?.id
                if (fcmToken.isNullOrBlank()) {
                    Log.w(
                        "FriendsRepository",
                        "상대방 FCM 토큰이 없어 푸시 알림을 건너뜁니다 (email=$email, uid=$targetUid)"
                    )
                    onMissing?.invoke()
                    return@addOnSuccessListener
                }
                onFoundToken(fcmToken)
            }
            .addOnFailureListener { e ->
                Log.e("FriendsRepository", "상대방 FCM 토큰 조회 실패 (email=$email)", e)
                onMissing?.invoke()
            }
    }

    private fun fetchFriendFcmTokenByUid(
        uid: String,
        onFoundToken: (String) -> Unit,
        onMissing: (() -> Unit)? = null
    ) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { snapshot ->
                val fcmToken = snapshot.getString("fcmToken")
                if (fcmToken.isNullOrBlank()) {
                    Log.w("FriendsRepository", "상대방 FCM 토큰이 없어 푸시 알림을 건너뜁니다 (uid=$uid)")
                    onMissing?.invoke()
                    return@addOnSuccessListener
                }
                onFoundToken(fcmToken)
            }
            .addOnFailureListener { e ->
                Log.e("FriendsRepository", "상대방 FCM 토큰 조회 실패 (uid=$uid)", e)
                onMissing?.invoke()
            }
    }

    private fun logTokenNotFound(requestEmail: String) {
        Log.w(
            "FriendsRepository",
            "친구 uid/email로 FCM 토큰을 찾지 못해 푸시 알림을 건너뜁니다 (requestEmail=$requestEmail)"
        )
    }

    private fun notifyFriendAdded(friendSummary: FriendSummary?, fallbackEmail: String) {
        if (friendSummary == null) {
            Log.w("FriendsRepository", "친구 정보가 없어 응답 없이 이메일로 토큰 조회를 시도합니다")
        }

        val currentUserName = AuthRepository.getCurrentUserProfile()?.username
            ?.takeIf { it.isNotBlank() } ?: "친구"

        val friendUid = friendSummary?.uuid
        val friendEmailFromResponse = friendSummary?.email?.takeIf { it.isNotBlank() }
        val emailFromRequest = fallbackEmail.trim()
        val onFoundToken: (String) -> Unit = { token ->
            FcmPushSender.sendNotification(
                targetToken = token,
                title = "토모 친구 추가",
                body = "$currentUserName 님이 친구로 추가했어요."
            )
        }

        val fallbackLookup: () -> Unit = {
            when {
                !friendUid.isNullOrBlank() -> {
                    fetchFriendFcmTokenByUid(friendUid, onFoundToken) {
                        if (!friendEmailFromResponse.isNullOrBlank()) {
                            fetchFriendFcmTokenByEmail(friendEmailFromResponse, onFoundToken) {
                                logTokenNotFound(emailFromRequest)
                            }
                        } else {
                            logTokenNotFound(emailFromRequest)
                        }
                    }
                }

                !friendEmailFromResponse.isNullOrBlank() -> {
                    fetchFriendFcmTokenByEmail(friendEmailFromResponse, onFoundToken) {
                        logTokenNotFound(emailFromRequest)
                    }
                }

                else -> {
                    Log.w(
                        "FriendsRepository",
                        "친구 uid와 이메일이 없어 푸시 알림을 보낼 수 없습니다 (requestEmail=$emailFromRequest)"
                    )
                    Unit
                }
            }
        }

        if (emailFromRequest.isNotBlank()) {
            fetchFriendFcmTokenByEmail(emailFromRequest, onFoundToken, fallbackLookup)
        } else {
            fallbackLookup()
        }
    }
}
