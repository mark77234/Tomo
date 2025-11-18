package com.markoala.tomoandroid.ui.main.friends

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.data.api.friendsApi
import com.markoala.tomoandroid.data.model.friends.FriendProfile
import com.markoala.tomoandroid.data.model.user.BaseResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsViewModel(application: Application) : AndroidViewModel(application) {
    private val _friends = MutableStateFlow<List<FriendProfile>>(emptyList()) // 내부 변경용
    val friends: StateFlow<List<FriendProfile>> = _friends.asStateFlow() // 외부 노출용

    private val _isLoading = MutableStateFlow(false) // 로딩 상태 표시용 flow
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow() // 외부 노출용

    private val _error = MutableStateFlow<String?>(null) // 에러 메시지 저장용 flow
    val error: StateFlow<String?> = _error.asStateFlow() // 외부 노출용

    // 가장 최근 요청만 반영하기 위한 토큰
    private var lastRequestId = 0L

    fun loadFriends() {
        val requestId = ++lastRequestId
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            friendsApi.getFriendsList()
                .enqueue(object : Callback<BaseResponse<List<FriendProfile>>> {
                    override fun onResponse(
                        call: Call<BaseResponse<List<FriendProfile>>>,
                        response: Response<BaseResponse<List<FriendProfile>>>
                    ) {
                        // 이미 더 새로운 요청이 진행 중이면 무시
                        if (requestId != lastRequestId) return

                        _isLoading.value = false
                        if (response.isSuccessful) {
                            response.body()?.let { friendListResponse ->
                                if (friendListResponse.success) {
                                    _friends.value = friendListResponse.data
                                } else {
                                    _error.value = "서버 오류: ${friendListResponse.message}"
                                }
                            } ?: run {
                                _error.value = "서버 응답이 비어있습니다."
                            }
                        } else {
                            if (response.code() == 401) {
                                // 401 에러 발생 시 자동 로그아웃 처리
                                viewModelScope.launch {
                                    AuthManager.handleUnauthorized(getApplication())
                                }
                                return
                            }

                            val errorMessage = when (response.code()) {
                                401 -> "인증이 필요합니다. 다시 로그인해주세요."
                                403 -> "접근 권한이 없습니다."
                                404 -> "서버를 찾을 수 없습니다."
                                500 -> "서버 내부 오류가 발생했습니다."
                                503 -> "서버가 일시적으로 사용할 수 없습니다."
                                else -> "친구 목록을 불러오는데 실패했습니다. (오류 코드: ${response.code()})"
                            }
                            _error.value = errorMessage
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<List<FriendProfile>>>,
                        t: Throwable
                    ) {
                        if (requestId != lastRequestId) return
                        _isLoading.value = false
                        val errorMessage = when {
                            t is java.net.UnknownHostException -> "인터넷 연결을 확인해주세요."
                            t is java.net.SocketTimeoutException -> "서버 응답 시간이 초과되었습니다."
                            t is java.net.ConnectException -> "서버에 연결할 수 없습니다."
                            t.message?.contains("SSL") == true -> "보안 연결에 문제가 있습니다."
                            else -> "네트워크 오류: ${t.message ?: "알 수 없는 오류"}"
                        }
                        _error.value = errorMessage
                    }
                })
        }
    }

    fun refreshFriends() {
        loadFriends()
    }
}
