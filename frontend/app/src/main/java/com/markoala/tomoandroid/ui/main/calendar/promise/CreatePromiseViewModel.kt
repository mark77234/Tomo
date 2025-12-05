package com.markoala.tomoandroid.ui.main.calendar.promise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.data.api.GeocodeAddress
import com.markoala.tomoandroid.data.api.MoimsApiService
import com.markoala.tomoandroid.data.api.PromiseApiService
import com.markoala.tomoandroid.data.model.MoimListDTO
import com.markoala.tomoandroid.data.model.PromiseDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CreatePromiseViewModel : ViewModel() {
    private val _moims = MutableStateFlow<List<MoimListDTO>>(emptyList())
    val moims: StateFlow<List<MoimListDTO>> = _moims

    private val _selectedMoim = MutableStateFlow<MoimListDTO?>(null)
    val selectedMoim: StateFlow<MoimListDTO?> = _selectedMoim

    val promiseName = MutableStateFlow("")
    private val _promiseDate = MutableStateFlow("")
    val promiseDate: StateFlow<String> = _promiseDate
    private val _promiseTime = MutableStateFlow<String?>(null)
    val promiseTime: StateFlow<String?> = _promiseTime

    private val _place = MutableStateFlow<String?>(null)
    val place: StateFlow<String?> = _place

    private val _selectedAddress = MutableStateFlow<GeocodeAddress?>(null)
    val selectedAddress: StateFlow<GeocodeAddress?> = _selectedAddress

    private val _selectedQuery = MutableStateFlow<String?>(null)
    val selectedQuery: StateFlow<String?> = _selectedQuery

    private val _isFetchingMoims = MutableStateFlow(false)
    val isFetchingMoims: StateFlow<Boolean> = _isFetchingMoims

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _isSuccess = MutableStateFlow<Boolean?>(null)
    val isSuccess: StateFlow<Boolean?> = _isSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchLeaderMoims()
    }

    fun setSelectedDate(date: LocalDate) {
        _promiseDate.value = date.toString()
    }

    fun selectMoim(moim: MoimListDTO) {
        _selectedMoim.value = moim
        clearError()
    }

    fun updatePromiseName(name: String) {
        promiseName.value = name
        clearError()
    }

    fun setPromiseTime(time: LocalTime) {
        _promiseTime.value = time.format(DateTimeFormatter.ofPattern("HH:mm"))
        clearError()
    }

    fun updateLocation(query: String, address: GeocodeAddress) {
        _selectedQuery.value = query
        _selectedAddress.value = address
        _place.value = address.displayTitle()
        clearError()
    }

    fun fetchLeaderMoims() {
        viewModelScope.launch {
            _isFetchingMoims.value = true
            try {
                val response = MoimsApiService.getMoimsList().awaitResponse()
                if (response.isSuccessful) {
                    val moims = response.body()?.data.orEmpty().filter { it.leader }
                    val previous = _selectedMoim.value
                    _moims.value = moims
                    _selectedMoim.value = moims.find { it.moimId == previous?.moimId } ?: moims.firstOrNull()
                } else {
                    _errorMessage.value = "모임 목록을 불러오지 못했어요."
                }
            } catch (e: Exception) {
                _errorMessage.value = "모임 목록을 불러오는 중 문제가 발생했어요."
            } finally {
                _isFetchingMoims.value = false
            }
        }
    }

    fun createPromise() {
        val moim = _selectedMoim.value
        val name = promiseName.value.trim()
        val date = _promiseDate.value
        val time = _promiseTime.value
        val placeValue = _place.value?.trim()

        if (moim == null) {
            _errorMessage.value = "약속을 만들 모임을 선택해주세요."
            return
        }
        if (name.isBlank()) {
            _errorMessage.value = "약속 이름을 입력해주세요."
            return
        }
        if (date.isBlank()) {
            _errorMessage.value = "약속 날짜가 선택되지 않았어요."
            return
        }
        if (time.isNullOrBlank()) {
            _errorMessage.value = "약속 시간을 선택해주세요."
            return
        }
        if (placeValue.isNullOrBlank()) {
            _errorMessage.value = "약속 장소를 선택해주세요."
            return
        }

        _isSubmitting.value = true
        _errorMessage.value = null
        _isSuccess.value = null

        viewModelScope.launch {
            try {
                val dto = PromiseDTO(
                    title = moim.title,
                    promiseName = name,
                    promiseDate = date,
                    promiseTime = time,
                    place = placeValue
                )
                val response = PromiseApiService.createPromise(dto).awaitResponse()
                if (response.isSuccessful && response.body()?.success == true) {
                    _isSuccess.value = true
                } else {
                    _isSuccess.value = false
                    _errorMessage.value = response.body()?.message
                        ?: response.errorBody()?.string()
                        ?: "약속을 생성하지 못했어요."
                }
            } catch (e: Exception) {
                _isSuccess.value = false
                _errorMessage.value = "약속 생성 중 문제가 발생했어요."
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun consumeSuccess() {
        _isSuccess.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

private fun GeocodeAddress.displayTitle(): String {
    return name?.takeIf { it.isNotBlank() }
        ?: roadAddress?.takeIf { it.isNotBlank() }
        ?: jibunAddress?.takeIf { it.isNotBlank() }
        ?: englishAddress?.takeIf { it.isNotBlank() }
        ?: "선택한 장소"
}
