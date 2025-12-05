package com.markoala.tomoandroid.ui.main.meeting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.data.api.MoimsApiService
import com.markoala.tomoandroid.data.model.MoimListDTO
import com.markoala.tomoandroid.data.model.BaseResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class MeetingViewModel : ViewModel() {
    private val _meetings = MutableStateFlow<List<MoimListDTO>>(emptyList())
    val meetings: StateFlow<List<MoimListDTO>> = _meetings

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchMeetings()
    }

    fun fetchMeetings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = MoimsApiService.getMoimsList().awaitResponse()
                if (response.isSuccessful) {
                    val body: BaseResponse<List<MoimListDTO>>? = response.body()
                    val meetings = body?.data ?: emptyList()
                    _meetings.value = meetings.map {
                        MoimListDTO(
                            moimId = it.moimId, // 추가
                            title = it.title,
                            description = it.description,
                            peopleCount = it.peopleCount,
                            createdAt = it.createdAt,
                            leader = it.leader
                        )
                    }
                }
            } catch (e: Exception) {
                // 에러 처리 필요시 추가
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMeeting(moimId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = MoimsApiService.deleteMoim(moimId).awaitResponse()
                if (response.isSuccessful) {
                    // 삭제 성공 시 리스트 갱신
                    fetchMeetings()
                }
            } catch (e: Exception) {
                // 에러 처리 필요시 추가
            } finally {
                _isLoading.value = false
            }
        }
    }
}