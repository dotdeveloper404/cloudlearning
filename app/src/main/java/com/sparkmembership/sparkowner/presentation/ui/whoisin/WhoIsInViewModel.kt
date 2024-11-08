package com.sparkmembership.sparkowner.presentation.ui.whoisin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.StaffMemberTimeClockDto
import com.sparkmembership.sparkowner.data.response.TimeSlipHistoryDto
import com.sparkmembership.sparkowner.domain.usecases.whoisin.WhoIsInUseCase
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WhoIsInViewModel @Inject constructor(
    private val whoIsInUseCase: WhoIsInUseCase,
) : BaseViewModel() {

    private val _staffMemberTimeClock = MutableLiveData<Resource<StaffMemberTimeClockDto>>()
    val staffMemberTimeClock: LiveData<Resource<StaffMemberTimeClockDto>> get() = _staffMemberTimeClock

    private var staffMemberTimeClockJob: Job? = null
    suspend fun getStaffMemberTimeClock(
        date: String,
    ) {

        if (staffMemberTimeClockJob != null) staffMemberTimeClockJob?.cancel()
        staffMemberTimeClockJob = whoIsInUseCase.getStaffMemberTimeClock(date).onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _staffMemberTimeClock.value = result
                }

                is Resource.Loading -> {
                    _staffMemberTimeClock.value = result

                }

                is Resource.Success ->  {
                    _staffMemberTimeClock.value = result
                }
            }
        }.launchIn(viewModelScope)
    }

}