package com.markoala.tomoandroid.ui.main.meeting.meeting_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.data.api.MoimsApiService
import com.markoala.tomoandroid.data.model.moim.MoimDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MeetingDetailViewModel : ViewModel() {
    private val _moimDetails = MutableStateFlow<MoimDetails?>(null)
    val moimDetails: StateFlow<MoimDetails?> = _moimDetails

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchMoimDetails(moimId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = withContext(Dispatchers.IO) {
                    MoimsApiService.getMoimDetails(moimId).execute()
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        _moimDetails.value = body.data
                        Log.d("MeetingDetailViewModel", "모임 상세 정보 로드 성공: ${body.data}")
                    } else {
                        _errorMessage.value = body?.message ?: "모임 정보를 불러오지 못했습니다."
                        Log.e("MeetingDetailViewModel", "API 실패: ${body?.message}")
                    }
                } else {
                    _errorMessage.value = "서버 오류: ${response.code()}"
                    Log.e("MeetingDetailViewModel", "HTTP 오류: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "네트워크 오류: ${e.message}"
                Log.e("MeetingDetailViewModel", "예외 발생", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
