package com.sparkmembership.sparkowner.presentation.ui.rateUs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sparkmembership.sparkowner.data.request.RateUsReqDTO
import com.sparkmembership.sparkowner.data.response.RateUsResDTO
import com.sparkmembership.sparkowner.domain.usecases.RateUsUsecase
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
data class RateUsViewModel @Inject constructor(
    private val rateUsUsecase: RateUsUsecase
) : BaseViewModel(){

    private val _rateUsData = MutableLiveData<Resource<RateUsResDTO>>()
    val rateUsData: LiveData<Resource<RateUsResDTO>> get() = _rateUsData

    var rateUsJob: Job? = null
    suspend fun rateUs(rateUsReqDTO: RateUsReqDTO) {
        if (rateUsJob != null) rateUsJob?.cancel()

        rateUsJob =  rateUsUsecase.userRating(rateUsReqDTO).onEach { response ->
            when (response) {
                is Resource.Error -> {
                    _rateUsData.value = response
                }

                is Resource.Loading -> {
                    _rateUsData.value = response
                }

                is Resource.Success -> {
                    _rateUsData.value = response
                }
            }

        }.launchIn(viewModelScope)
    }
}
