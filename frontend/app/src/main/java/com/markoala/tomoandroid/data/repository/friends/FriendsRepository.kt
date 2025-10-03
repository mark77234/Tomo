package com.markoala.tomoandroid.data.repository.friends

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.data.api.apiService
import com.markoala.tomoandroid.data.model.FriendData
import com.markoala.tomoandroid.data.model.FriendSearchRequest
import com.markoala.tomoandroid.data.model.FriendSearchResponse
import com.markoala.tomoandroid.data.model.GetFriendsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsRepository {

    // 친구 검색 함수 (GET)
    fun getFriends(
        email: String,
        context: Context,
        onLoading: (Boolean) -> Unit,
        onSuccess: (List<FriendData>) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("FriendsRepository", "getFriends 시작 - 입력된 이메일: $email")

        if (email.isBlank()) {
            Log.w("FriendsRepository", "이메일이 비어있음")
            Toast.makeText(context, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        onLoading(true)
        Log.d("FriendsRepository", "검색 상태 변경: loading = true")

        // Firebase ID 토큰 가져오기
        Log.d("FriendsRepository", "Firebase 토큰 요청 시작")
        AuthManager.auth.currentUser?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
            if (tokenTask.isSuccessful) {
                val firebaseToken = tokenTask.result?.token
                Log.d("FriendsRepository", "Firebase 토큰 획득 성공")

                if (firebaseToken != null) {
                    // API 요청
                    Log.d("FriendsRepository", "getFriends API 요청 생성")

                    val call = apiService.getFriends("Bearer $firebaseToken", email)
                    Log.d("FriendsRepository", "getFriends API 호출 시작")

                    call.enqueue(object : Callback<GetFriendsResponse> {
                        override fun onResponse(
                            call: Call<GetFriendsResponse>,
                            response: Response<GetFriendsResponse>
                        ) {
                            Log.d("FriendsRepository", "getFriends API 응답 수신")
                            Log.d("FriendsRepository", "응답 코드: ${response.code()}")

                            onLoading(false)
                            if (response.isSuccessful) {
                                val result = response.body()
                                Log.d("FriendsRepository", "응답 본문: $result")

                                if (result?.success == true && result.data != null) {
                                    Log.d(
                                        "FriendsRepository",
                                        "친구 검색 성공 - 찾은 친구: ${result.data.username}"
                                    )
                                    onSuccess(listOf(result.data)) // 단일 객체를 리스트로 변환
                                } else {
                                    Log.w("FriendsRepository", "친구 검색 실패")
                                    onError(result?.message ?: "친구를 찾을 수 없습니다")
                                }
                            } else {
                                Log.e("FriendsRepository", "HTTP 응답 실패 - 코드: ${response.code()}")
                                onError("검색에 실패했습니다")
                            }
                        }

                        override fun onFailure(call: Call<GetFriendsResponse>, t: Throwable) {
                            Log.e("FriendsRepository", "getFriends API 요청 실패", t)
                            onLoading(false)
                            onError("네트워크 오류가 발생했습니다")
                        }
                    })
                } else {
                    Log.e("FriendsRepository", "Firebase 토큰이 null")
                    onLoading(false)
                    Toast.makeText(context, "인증 토큰을 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("FriendsRepository", "Firebase 토큰 획득 실패", tokenTask.exception)
                onLoading(false)
                Toast.makeText(context, "인증에 실패했습니다", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Log.e("FriendsRepository", "현재 사용자가 null - 로그인되지 않음")
            onLoading(false)
            Toast.makeText(context, "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 친구 추가 함수 (POST)
    fun postFriends(
        email: String,
        context: Context,
        onLoading: (Boolean) -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("FriendsRepository", "postFriends 시작 - 입력된 이메일: $email")

        if (email.isBlank()) {
            Log.w("FriendsRepository", "이메일이 비어있음")
            Toast.makeText(context, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        onLoading(true)
        Log.d("FriendsRepository", "친구 추가 상태 변경: loading = true")

        // Firebase ID 토큰 가져오기
        Log.d("FriendsRepository", "Firebase 토큰 요청 시작")
        AuthManager.auth.currentUser?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
            if (tokenTask.isSuccessful) {
                val firebaseToken = tokenTask.result?.token
                Log.d("FriendsRepository", "Firebase 토큰 획득 성공")
                Log.d("FriendsRepository", "토큰 길이: ${firebaseToken?.length ?: 0}")
                Log.d("FriendsRepository", "토큰 앞 20자: ${firebaseToken?.take(20) ?: "null"}...")

                if (firebaseToken != null) {
                    // API 요청
                    val request = FriendSearchRequest(email = email)
                    Log.d("FriendsRepository", "API 요청 생성 - 요청 데이터: $request")
                    Log.d(
                        "FriendsRepository",
                        "Authorization 헤더: Bearer ${firebaseToken.take(20)}..."
                    )

                    val call = apiService.postFriends("Bearer $firebaseToken", request)
                    Log.d("FriendsRepository", "API 호출 시작 - URL: ${call.request().url}")
                    Log.d("FriendsRepository", "HTTP 메소드: ${call.request().method}")

                    call.enqueue(object : Callback<FriendSearchResponse> {
                        override fun onResponse(
                            call: Call<FriendSearchResponse>,
                            response: Response<FriendSearchResponse>
                        ) {
                            Log.d("FriendsRepository", "API 응답 수신")
                            Log.d("FriendsRepository", "응답 코드: ${response.code()}")
                            Log.d("FriendsRepository", "응답 메시지: ${response.message()}")
                            Log.d("FriendsRepository", "응답 성공 여부: ${response.isSuccessful}")

                            onLoading(false)
                            if (response.isSuccessful) {
                                val result = response.body()
                                Log.d("FriendsRepository", "응답 본문: $result")

                                if (result?.success == true && result.data != null) {
                                    Log.d(
                                        "FriendsRepository",
                                        "친구 추가 성공 - 사용자명: ${result.data.username}, 이메일: ${result.data.email}"
                                    )
                                    Toast.makeText(context, "친구 추가 성공!", Toast.LENGTH_SHORT).show()
                                    onSuccess()
                                } else {
                                    Log.w(
                                        "FriendsRepository",
                                        "친구 추가 실패 - success: ${result?.success}, data: ${result?.data}, message: ${result?.message}"
                                    )
                                    val errorMsg = result?.message ?: "친구 추가에 실패했습니다"
                                    onError(errorMsg)
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Log.e(
                                    "FriendsRepository",
                                    "HTTP 응답 실패 - 코드: ${response.code()}, 메시지: ${response.message()}"
                                )
                                Log.e(
                                    "FriendsRepository",
                                    "에러 본문: ${response.errorBody()?.string()}"
                                )
                                onError("친구 추가에 실패했습니다")
                                Toast.makeText(context, "친구 추가에 실패했습니다", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<FriendSearchResponse>, t: Throwable) {
                            Log.e("FriendsRepository", "API 요청 완전 실패", t)
                            Log.e("FriendsRepository", "실패 원인: ${t.javaClass.simpleName}")
                            Log.e("FriendsRepository", "에러 메시지: ${t.message}")
                            Log.e("FriendsRepository", "요청 URL: ${call.request().url}")

                            onLoading(false)
                            onError("네트워크 오류")
                            Toast.makeText(context, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Log.e("FriendsRepository", "Firebase 토큰이 null")
                    onLoading(false)
                    Toast.makeText(context, "인증 토큰을 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("FriendsRepository", "Firebase 토큰 획득 실패", tokenTask.exception)
                Log.e(
                    "FriendsRepository",
                    "토큰 태스크 예외: ${tokenTask.exception?.javaClass?.simpleName}"
                )
                Log.e("FriendsRepository", "토큰 태스크 메시지: ${tokenTask.exception?.message}")

                onLoading(false)
                Toast.makeText(context, "인증에 실패했습니다", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Log.e("FriendsRepository", "현재 사용자가 null - 로그인되지 않음")
            onLoading(false)
            Toast.makeText(context, "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }
}