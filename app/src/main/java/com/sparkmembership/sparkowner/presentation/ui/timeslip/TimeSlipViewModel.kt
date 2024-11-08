package com.sparkmembership.sparkowner.presentation.ui.timeslip

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.ClockInDto
import com.sparkmembership.sparkowner.data.response.ClockOutDto
import com.sparkmembership.sparkowner.data.response.TimeSlipHistoryDto
import com.sparkmembership.sparkowner.data.response.TimeSlipUserHistoryDto
import com.sparkmembership.sparkowner.data.response.UserTimeSlipDto
import com.sparkmembership.sparkowner.domain.usecases.clockInAndOut.ClockInAndOutUseCase
import com.sparkmembership.sparkowner.domain.usecases.timeslip.TimeSlipHistoryUseCase
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TimeSlipViewModel  @Inject constructor(
    private val timeSlipUseCase:TimeSlipHistoryUseCase,
    private val clockInAndOutUseCase : ClockInAndOutUseCase,
    private val appConfig: AppConfig
) : BaseViewModel(){

    private val _timeSlipHistoryDto = MutableLiveData<Resource<TimeSlipHistoryDto>>()
    val timeSlipHistoryDto: LiveData<Resource<TimeSlipHistoryDto>> get() = _timeSlipHistoryDto

    private val _timeSlipDetailDto = MutableLiveData<Resource<TimeSlipUserHistoryDto>>()
    val timeSlipDetailDto: LiveData<Resource<TimeSlipUserHistoryDto>> get() = _timeSlipDetailDto

    private val _userTimeSlipHistory = MutableLiveData<Resource<UserTimeSlipDto>>()
    val userTimeSlipHistory: LiveData<Resource<UserTimeSlipDto>> get() = _userTimeSlipHistory

    private val _clockIn = MutableLiveData<Resource<ClockInDto>>()
    val clockIn: LiveData<Resource<ClockInDto>> get() = _clockIn

    private val _clockOut = MutableLiveData<Resource<ClockOutDto>>()
    val clockOut: LiveData<Resource<ClockOutDto>> get() = _clockOut

    var userLocation: Location? = null

    private val _LatLongLocation =
        MutableLiveData<com.sparkmembership.sparkowner.data.response.Result>(appConfig.SIGN_IN)

    init {
        setUserLocation(_LatLongLocation.value?.userDetails?.latitude.toString(),
            _LatLongLocation.value?.userDetails?.longitude.toString()
        )
    }

    private var timeSlipHistoryJob: Job? = null
    suspend fun timeSlipHistory(
        pageSize: Int ? = null,
        pageIndex: Int? = null,
        startDate: String? = null,
        endDate: String? = null,
        userName: String? = null,
    ) {

        if (timeSlipHistoryJob != null) timeSlipHistoryJob?.cancel()
        timeSlipHistoryJob = timeSlipUseCase.timeSlipHistory(pageIndex = pageIndex, pageSize = pageSize,startDate = startDate,endDate = endDate,userName = userName).onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _timeSlipHistoryDto.value = result
                }

                is Resource.Loading -> {
                    _timeSlipHistoryDto.value = result

                }

                is Resource.Success ->  {
                    _timeSlipHistoryDto.value = result
                }
            }
        }.launchIn(viewModelScope)
    }

    private var timeSlipHistoryByIdJob: Job? = null
    suspend fun timeSlipHistoryById(
        pageSize: Int ? = null,
        pageIndex: Int? = null,
        startDate: String? = null,
        endDate: String? = null,
        userId: Int,
    ) {

        if (timeSlipHistoryByIdJob != null) timeSlipHistoryByIdJob?.cancel()
        timeSlipHistoryJob = timeSlipUseCase.timeSlipHistoryById(pageIndex = pageIndex, pageSize = pageSize,startDate = startDate,endDate = endDate,userId = userId).onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _timeSlipDetailDto.value = result
                }

                is Resource.Loading -> {
                    _timeSlipDetailDto.value = result

                }

                is Resource.Success ->  {
                    _timeSlipDetailDto.value = result
                }
            }
        }.launchIn(viewModelScope)
    }

    private var userTimeSlipHistoryJob: Job? = null
    suspend fun userTimeSlipHistory() {
        if (userTimeSlipHistoryJob != null) userTimeSlipHistoryJob?.cancel()
        timeSlipHistoryJob = timeSlipUseCase.userTimeSlipHistory().onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _userTimeSlipHistory.value = result
                }

                is Resource.Loading -> {
                    _userTimeSlipHistory.value = result

                }

                is Resource.Success ->  {
                    _userTimeSlipHistory.value = result
                }
            }
        }.launchIn(viewModelScope)
    }

    private var clockInJob: Job? = null
    suspend fun clockIn() {
        if (clockInJob != null) clockInJob?.cancel()
        clockInJob = clockInAndOutUseCase.clockIn().onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _clockIn.value = result
                }

                is Resource.Loading -> {
                    _clockIn.value = result

                }

                is Resource.Success ->  {
                    _clockIn.value = result
                }
            }
        }.launchIn(viewModelScope)
    }

    private var clockOutJob: Job? = null
    suspend fun clockOut() {
        if (clockOutJob != null) clockOutJob?.cancel()
        clockOutJob = clockInAndOutUseCase.clockOut().onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _clockOut.value = result
                }

                is Resource.Loading -> {
                    _clockOut.value = result

                }

                is Resource.Success ->  {
                    _clockOut.value = result
                }
            }
        }.launchIn(viewModelScope)
    }


    private fun setUserLocation(latitude: String, longitude: String) {

        if (latitude.isNotEmpty() && longitude.isNotEmpty())
            userLocation = Location("").apply {
                this.latitude = latitude.toDouble()
                this.longitude = longitude.toDouble()
            }
    }
}