package com.sparkmembership.sparkowner.presentation.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.constant.KEY_CONNECTED_LOCATION
import com.sparkmembership.sparkowner.constant.KEY_SIGN_DETAIL
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ChangeConnectedLocationReqDTO
import com.sparkmembership.sparkowner.data.request.SignInReqDTO
import com.sparkmembership.sparkowner.data.response.ConnectedLocation
import com.sparkmembership.sparkowner.data.response.SignInResDto
import com.sparkmembership.sparkowner.domain.repository.LocalRepository
import com.sparkmembership.sparkowner.domain.usecases.signIn.SignInUseCase
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectedLocationViewModel @Inject constructor(
    private val appConfig: AppConfig,
    private val localRepository: LocalRepository,
    private val signInUseCase: SignInUseCase
):BaseViewModel() {


    private val _signInMutableData = MutableLiveData<SignInResDto>()
    val connectedLocationsLiveData: LiveData<SignInResDto> get() = _signInMutableData

    fun setSignInData(signInResDto: SignInResDto) {
        _signInMutableData.value = signInResDto
    }



    private val _changeConnectedLocation = MutableLiveData<Resource<SignInResDto?>>()
    val changeConnectedLocation: LiveData<Resource<SignInResDto?>> get() = _changeConnectedLocation

    private var connectedLocationJob: Job? = null

    suspend fun changeConnectedLocation(changeConnectedLocationReqDTO: ChangeConnectedLocationReqDTO, connectedLocation: ConnectedLocation) {

        if (connectedLocationJob != null) connectedLocationJob?.cancel()
        connectedLocationJob = signInUseCase.changeConnectedLocation(changeConnectedLocationReqDTO).onEach { response ->
            when(response){
                is Resource.Error -> {
                    _changeConnectedLocation.value = response
                }
                is Resource.Loading -> {
                    _changeConnectedLocation.value = response

                }
                is Resource.Success -> {
                    _changeConnectedLocation.value = response

                    val locationID = response.data?.result?.userDetails?.locationID
                    val contactID = response.data?.result?.userDetails?.id

                    FirebaseCrashlytics.getInstance()
                        .setUserId(locationID.toString() + "-" + contactID + "-" + "email")

                    appConfig.AUTH_TOKEN = response.data!!.result.token.accessToken
                    appConfig.IS_LOGGED_IN = !response.data.result.token.accessToken.isNullOrEmpty()
                    appConfig.SIGN_IN = response.data.result
                    appConfig.CONNECTED_LOCATION = connectedLocation


                    localRepository.writeObject(KEY_SIGN_DETAIL,response.data.result)
                    localRepository.writeObject(KEY_CONNECTED_LOCATION, connectedLocation)

                }
            }
        }.launchIn(viewModelScope)

    }





}