package com.sparkmembership.sparkowner.presentation.ui.forgotPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sparkmembership.sparkowner.data.remote.Event
import com.sparkmembership.sparkowner.data.request.ForgotPasswordReqDTO
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.domain.usecases.ForgotPasswordUsecase
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import com.sparkmembership.sparkowner.util.getFirebaseToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUsecase
):BaseViewModel() {

    private val _forgotPassword = MutableLiveData<Event<Resource<BaseResponse?>>>()
    val forgotPasswordLiveData: LiveData<Event<Resource<BaseResponse?>>> get() = _forgotPassword

    private var forgotPasswordJob: Job? = null

    suspend fun forgotPassword(forgotPasswordReqDTO: ForgotPasswordReqDTO) {
        // Cancel any existing job before starting a new one
        forgotPasswordJob?.cancel()

        // Launch a new job to handle the forgot password API request
        forgotPasswordJob = viewModelScope.launch {
            forgotPasswordUseCase.forgotPassword(forgotPasswordReqDTO).collect { response ->
                when (response) {
                    is Resource.Error -> {
                        _forgotPassword.postValue(Event(response))
                    }
                    is Resource.Loading -> {
                        _forgotPassword.postValue(Event(response))
                    }
                    is Resource.Success -> {
                        _forgotPassword.postValue(Event(response))
                    }
                }
            }
        }
    }



    suspend fun fogetPasswordObject(email: String): ForgotPasswordReqDTO {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                getFirebaseToken { token ->
                    continuation.resume(ForgotPasswordReqDTO(
                        email = email,
                        locationID = 0,
                        pushID = token
                    ))
                }
            }
        }
    }




}