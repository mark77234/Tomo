package com.markoala.tomoandroid.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.data.api.MoimsApiService
import com.markoala.tomoandroid.data.model.moim.Meeting
import com.markoala.tomoandroid.data.model.moim.MoimDTO
import com.markoala.tomoandroid.data.model.user.BaseResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class HomeViewModel : ViewModel() {
    private val _meetings = MutableStateFlow<List<Meeting>>(emptyList())
    val meetings: StateFlow<List<Meeting>> = _meetings

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
                    val body: BaseResponse<List<MoimDTO>>? = response.body()
                    val moims = body?.data ?: emptyList()
                    _meetings.value = moims.map {
                        Meeting(
                            title = it.moimName,
                            location = it.description, // description을 location에 매핑
                            time = null, // 시간 정보 없음
                            peopleCounts = it.peopleCounts
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
}
