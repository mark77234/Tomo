package com.markoala.tomoandroid.ui.main.meeting.create_meeting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.data.api.MoimsApiService
import com.markoala.tomoandroid.data.api.friendsApi
import com.markoala.tomoandroid.data.model.FriendProfile
import com.markoala.tomoandroid.data.model.CreateMoimDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.awaitResponse

class CreateMeetingViewModel(application: Application) : AndroidViewModel(application) {
    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    private val _friends = MutableStateFlow<List<FriendProfile>>(emptyList())
    val friends: StateFlow<List<FriendProfile>> = _friends
    private val _selectedEmails = MutableStateFlow<Set<String>>(emptySet())
    val selectedEmails: StateFlow<Set<String>> = _selectedEmails
    val isLoading = MutableStateFlow(false)
    val isSuccess = MutableStateFlow<Boolean?>(null)
    val errorMessage = MutableStateFlow<String?>(null)

    init {
        fetchFriends()
    }

    fun fetchFriends() {
        viewModelScope.launch {
            try {
                val response = friendsApi.getFriendsList().awaitResponse()
                if (response.isSuccessful) {
                    _friends.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                if (e is HttpException && e.code() == 401) {
                    AuthManager.handleUnauthorized(getApplication())
                    errorMessage.value = "인증이 필요합니다. 다시 로그인해주세요."
                } else {
                    errorMessage.value = "친구 목록을 불러오지 못했습니다."
                }
            }
        }
    }

    fun toggleEmail(email: String) {
        _selectedEmails.value = _selectedEmails.value.toMutableSet().apply {
            if (contains(email)) remove(email) else add(email)
        }
    }

    fun createMoim() {
        if (title.value.isBlank() || description.value.isBlank()) {
            errorMessage.value = "모임 이름과 설명을 입력하세요."
            return
        }
        isLoading.value = true
        errorMessage.value = null
        isSuccess.value = null
        viewModelScope.launch {
            try {
                val emails = selectedEmails.value.filter { it.isNotBlank() }.distinct().toList()
                val dto = CreateMoimDTO(
                    title = title.value,
                    description = description.value,
                    emails = emails
                )
                val response = MoimsApiService.postMoim(dto).awaitResponse()
                isLoading.value = false
                isSuccess.value = response.isSuccessful
                if (!response.isSuccessful) {
                    errorMessage.value = response.errorBody()?.string() ?: "생성 실패"
                }
            } catch (e: Exception) {
                isLoading.value = false
                isSuccess.value = false
                errorMessage.value = "네트워크 오류"
            }
        }
    }

    fun consumeSuccess() {
        isSuccess.value = null
    }

    fun clearError() {
        errorMessage.value = null
    }

    fun resetAllData() {
        title.value = ""
        description.value = ""
        _selectedEmails.value = emptySet()
        errorMessage.value = null
        isSuccess.value = null
        isLoading.value = false
    }
}
