package com.sparkmembership.sparkowner.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.data.request.ContactFilterResult
import com.sparkmembership.sparkowner.data.request.FilterStringObject
import com.sparkmembership.sparkowner.data.response.SignInResDto
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val appConfig: AppConfig
) :BaseViewModel() {


    private val _signInData = MutableLiveData<SignInResDto>()
    val signInLiveData: LiveData<SignInResDto> get() = _signInData

    fun setSignInData(signInResDto: SignInResDto) {
        _signInData.value = signInResDto
    }


}