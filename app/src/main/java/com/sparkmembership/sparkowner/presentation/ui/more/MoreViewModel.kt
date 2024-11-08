package com.sparkmembership.sparkowner.presentation.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.data.response.ConnectedLocation
import com.sparkmembership.sparkowner.data.response.filter.Result
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoreViewModel  @Inject constructor(
    private val appConfig: AppConfig
): BaseViewModel(){

    private val _profileData  = MutableLiveData<com.sparkmembership.sparkowner.data.response.Result>(appConfig.SIGN_IN)
    val profileLiveData : LiveData<com.sparkmembership.sparkowner.data.response.Result> get() = _profileData


    private val _connectedLocation  = MutableLiveData<ConnectedLocation>(appConfig.CONNECTED_LOCATION)
    val connectedLocationLiveData : LiveData<ConnectedLocation> get() = _connectedLocation

}