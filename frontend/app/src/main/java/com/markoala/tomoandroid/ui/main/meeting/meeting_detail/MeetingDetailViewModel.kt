package com.markoala.tomoandroid.ui.main.meeting.meeting_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.data.api.MoimsApiService
import com.markoala.tomoandroid.data.api.friendsApi
import com.markoala.tomoandroid.data.model.MoimDetails
import com.markoala.tomoandroid.data.model.FriendProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async

data class MemberWithProfile(
    val email: String,
    val leader: Boolean,
    val profile: FriendProfile?
)

class MeetingDetailViewModel : ViewModel() {
    private val _moimDetails = MutableStateFlow<MoimDetails?>(null)
    val moimDetails: StateFlow<MoimDetails?> = _moimDetails

    private val _membersWithProfiles = MutableStateFlow<List<MemberWithProfile>>(emptyList())
    val membersWithProfiles: StateFlow<List<MemberWithProfile>> = _membersWithProfiles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val firebaseAuth = FirebaseAuth.getInstance()

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

                        // 멤버들의 프로필 정보 가져오기
                        body.data?.let { details ->
                            fetchMembersProfiles(details.members.map { it.email to it.leader })
                        }
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

    private suspend fun fetchMembersProfiles(members: List<Pair<String, Boolean>>) {
        try {
            val currentUserEmail = firebaseAuth.currentUser?.email
            val currentUserName = firebaseAuth.currentUser?.displayName

            val profiles = withContext(Dispatchers.IO) {
                members.map { (email, isLeader) ->
                    async {
                        try {
                            // 본인일 경우 API 호출 없이 본인 정보 사용
                            if (email == currentUserEmail) {
                                val selfProfile = FriendProfile(
                                    username = currentUserName ?: "나",
                                    email = email,
                                    friendship = 100,
                                    createdAt = "" // 본인은 친구 된 날짜가 없음
                                )
                                MemberWithProfile(email, isLeader, selfProfile)
                            } else {
                                // 친구 상세 정보 먼저 시도
                                val detailResponse = friendsApi.getFriendDetails(email).execute()
                                if (detailResponse.isSuccessful && detailResponse.body()?.success == true) {
                                    MemberWithProfile(email, isLeader, detailResponse.body()?.data)
                                } else {
                                    // 친구가 아닌 경우 getFriends로 기본 정보 가져오기
                                    val summaryResponse = friendsApi.getFriends(email).execute()
                                    if (summaryResponse.isSuccessful && summaryResponse.body()?.success == true) {
                                        val summary = summaryResponse.body()?.data
                                        val basicProfile = FriendProfile(
                                            username = summary?.username ?: "알 수 없음",
                                            email = summary?.email ?: email,
                                            friendship = 0, // 친구가 아니므로 0
                                            createdAt = "" // 친구가 아니므로 빈 값
                                        )
                                        MemberWithProfile(email, isLeader, basicProfile)
                                    } else {
                                        MemberWithProfile(email, isLeader, null)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MeetingDetailViewModel", "프로필 로드 실패: $email", e)
                            MemberWithProfile(email, isLeader, null)
                        }
                    }
                }.map { it.await() }
            }
            _membersWithProfiles.value = profiles
            Log.d("MeetingDetailViewModel", "멤버 프로필 로드 완료: ${profiles.size}개")
        } catch (e: Exception) {
            Log.e("MeetingDetailViewModel", "멤버 프로필 로드 중 오류", e)
        }
    }
}
