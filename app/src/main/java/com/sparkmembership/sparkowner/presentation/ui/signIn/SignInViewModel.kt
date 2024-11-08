package com.sparkmembership.sparkowner.presentation.ui.signIn

import android.app.Application
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.constant.KEY_CONNECTED_LOCATION
import com.sparkmembership.sparkowner.constant.KEY_SIGN_DETAIL
import com.sparkmembership.sparkowner.data.request.SignInReqDTO
import com.sparkmembership.sparkowner.data.response.SignInResDto
import com.sparkmembership.sparkowner.domain.repository.LocalRepository
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.ConnectedLocation
import com.sparkmembership.sparkowner.domain.usecases.signIn.SignInUseCase
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import com.sparkmembership.sparkowner.util.getFirebaseToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val appContext: Application,
    private val localRepository: LocalRepository,
    private val appConfig: AppConfig)
    :BaseViewModel() {

    private val _signInData = MutableLiveData<Resource<SignInResDto?>>()
    val signInLiveData: LiveData<Resource<SignInResDto?>> get() = _signInData

    private var signInJob: Job? = null

    suspend fun signIn(signInReqDTO: SignInReqDTO, callback:(signInResDto: SignInResDto) -> Unit) {

        if (signInJob != null) signInJob?.cancel()
        signInJob = signInUseCase.signIn(signInReqDTO).onEach { response ->
            when(response){
                is Resource.Error -> {
                    _signInData.value = response
                }
                is Resource.Loading -> {
                    _signInData.value = response

                }
                is Resource.Success -> {
                    _signInData.value = response
                    callback(response.data!!)
                    val locationID = response.data?.result?.userDetails?.locationID
                    val contactID = response.data?.result?.userDetails?.id

                    FirebaseCrashlytics.getInstance()
                        .setUserId(locationID.toString() + "-" + contactID + "-" + signInReqDTO.email)

                    if (response.data.result.userDetails.connectedLocations.isEmpty()){
                        val connetctedLocation = ConnectedLocation(
                            locationID = locationID!!,
                            locationName = response.data.result.userDetails.locationName,
                            userID = contactID!!
                            )

                        appConfig.AUTH_TOKEN = response.data.result.token.accessToken
                        appConfig.IS_LOGGED_IN = !response.data.result.token.accessToken.isNullOrEmpty()
                        appConfig.SIGN_IN = response.data.result
                        localRepository.writeObject(KEY_CONNECTED_LOCATION,connetctedLocation)
                        localRepository.writeObject(KEY_SIGN_DETAIL,response.data.result)

                    } else if (response.data.result.userDetails.connectedLocations.size==1){
                        appConfig.AUTH_TOKEN = response.data.result.token.accessToken
                        appConfig.IS_LOGGED_IN = !response.data.result.token.accessToken.isNullOrEmpty()
                        appConfig.SIGN_IN = response.data.result
                        appConfig.CONNECTED_LOCATION = response.data.result.userDetails.connectedLocations.first()
                        localRepository.writeObject(KEY_SIGN_DETAIL,response.data.result)
                        localRepository.writeObject(KEY_CONNECTED_LOCATION,response.data.result.userDetails.connectedLocations.first())
                    }else if (response.data.result.userDetails.connectedLocations.size > 1){
                        appConfig.AUTH_TOKEN = response.data.result.token.accessToken
                    }

                }
            }
        }.launchIn(viewModelScope)

    }




    suspend fun getLoginDetails(email: String, password: String): SignInReqDTO? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                getFirebaseToken { token ->
                    continuation.resume(SignInReqDTO(
                        email = email,
                        password = password,
                        deviceID = Settings.Secure.getString(
                            appContext.contentResolver,
                            Settings.Secure.ANDROID_ID
                        ),
                        pushID = token
                    ))
                }
            }
        }
    }

}