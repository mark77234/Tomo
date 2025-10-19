package com.markoala.tomoandroid.data.repository.friends

import android.util.Log
import com.markoala.tomoandroid.data.api.friendsApi

import com.markoala.tomoandroid.data.model.friends.FriendSearchRequest

import com.markoala.tomoandroid.data.model.friends.FriendSummary
import com.markoala.tomoandroid.data.model.user.BaseResponse
import com.markoala.tomoandroid.utils.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsRepository {

    // 친구 검색 함수 (GET)
    fun getFriends(
        email: String,
        onLoading: (Boolean) -> Unit,
        onSuccess: (List<FriendSummary>) -> Unit,
        onError: (String) -> Unit
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
                        onError(result?.message ?: "친구를 찾을 수 없습니다")
                    }
                } else {
                    Log.e("FriendsRepository", "HTTP 응답 실패 - 코드: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("FriendsRepository", "에러 본문: $errorBody")

                    // HTTP 상태 코드에 따른 구체적인 에러 메시지 생성
                    val errorResult = ErrorHandler.handleHttpError(response.code(), errorBody)
                    onError(errorResult.message)
                }
            }

            override fun onFailure(call: Call<BaseResponse<FriendSummary>>, t: Throwable) {
                Log.e("FriendsRepository", "getFriends API 요청 실패", t)
                onLoading(false)
                onError("네트워크 오류가 발생했습니다")
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

        // AuthInterceptor가 자동으로 토큰을 추가하므로 직접 API 호출
        val request = FriendSearchRequest(email = email)
        Log.d("FriendsRepository", "API 요청 생성 - 요청 데이터: $request")

        val call = friendsApi.postFriends(request)
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
}
