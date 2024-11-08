package com.sparkmembership.sparkowner.presentation.ui.profile

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.constant.KEY_SIGN_DETAIL
import com.sparkmembership.sparkowner.data.remote.Event
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.LogoutReqDTO
import com.sparkmembership.sparkowner.data.request.ProfilePictureReqDTO
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.data.response.ConnectedLocation
import com.sparkmembership.sparkowner.data.response.filter.Result
import com.sparkmembership.sparkowner.data.response.UploadProfilePictureResDTO
import com.sparkmembership.sparkowner.domain.repository.LocalRepository
import com.sparkmembership.sparkowner.domain.usecases.LogoutUsecase
import com.sparkmembership.sparkowner.domain.usecases.ProfileUsecase
import com.sparkmembership.sparkowner.domain.usecases.ResetPasswordUsecase
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.util.GlideUtil
import com.sparkmembership.sparkowner.util.deleteCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appConfig: AppConfig,
    private val localRepository: LocalRepository,
    private val profileUsecase: ProfileUsecase,
    private val logoutUsecase: LogoutUsecase,
    private val resetPasswordUsecase: ResetPasswordUsecase,
    private val appContext: Application
) : BaseViewModel() {

    private var _profileData = MutableLiveData<com.sparkmembership.sparkowner.data.response.Result>(appConfig.SIGN_IN)
    val profileLiveData: LiveData<com.sparkmembership.sparkowner.data.response.Result> get() = _profileData


    private val _connectedLocation =
        MutableLiveData<ConnectedLocation>(appConfig.CONNECTED_LOCATION)
    val connectedLocationData: LiveData<ConnectedLocation> get() = _connectedLocation


    private val _upoloadProfilePictureData = MutableLiveData<Resource<UploadProfilePictureResDTO>>()
    val uploadProfilePictureData: LiveData<Resource<UploadProfilePictureResDTO>> get() = _upoloadProfilePictureData


    private var uploadProfilePictureJob: Job? = null

    suspend fun uploadPicture(profilePictureReqDTO: ProfilePictureReqDTO) {
        if (uploadProfilePictureJob != null) uploadProfilePictureJob?.cancel()

        uploadProfilePictureJob =
            profileUsecase.updateProfilePicture(profilePictureReqDTO).onEach { response ->
                when (response) {
                    is Resource.Error -> {
                        _upoloadProfilePictureData.value = response
                    }

                    is Resource.Loading -> {
                        _upoloadProfilePictureData.value = response
                    }

                    is Resource.Success -> {
                        _upoloadProfilePictureData.value = response

                        val profilePicture = response.data?.result?.pictureURL
                        val profilePictureUpdatedTime =
                            response.data?.result?.profilePictureUpdatedTime

                        val signInObject = appConfig.SIGN_IN

                        val newSignInObject = signInObject!!.copy(
                            userDetails = signInObject.userDetails.copy(
                            userImage = profilePicture!!,
                            profilePicLastUpdate = profilePictureUpdatedTime!!
                        ))

                        appConfig.SIGN_IN = newSignInObject

                        localRepository.writeObject(KEY_SIGN_DETAIL, newSignInObject)

                    }
                }
            }.launchIn(viewModelScope)
    }


    private val _logout = MutableLiveData<Event<Resource<BaseResponse>>>()
    val logout: LiveData<Event<Resource<BaseResponse>>> get() = _logout

    var logoutJob: Job? = null
    suspend fun logoutUser(logoutReqDTO: LogoutReqDTO) {
        if (logoutJob != null) logoutJob?.cancel()

        logoutJob = viewModelScope.launch {
            logoutUsecase.logout(logoutReqDTO).collect { response ->

                when (response) {
                    is Resource.Error -> {
                        _logout.postValue(Event(response))
                    }

                    is Resource.Loading -> {
                        _logout.postValue(Event(response))
                    }

                    is Resource.Success -> {
                        _logout.postValue(Event(response))

                    }
                }
            }
        }

    }

    private val _resetPassword = MutableLiveData<Event<Resource<BaseResponse>>>()
    val resetPasswordLiveData: LiveData<Event<Resource<BaseResponse>>> get() = _resetPassword

    var resetPasswordJob: Job? = null
    suspend fun resetPassword() {
        if (resetPasswordJob != null) resetPasswordJob?.cancel()

        resetPasswordJob = viewModelScope.launch {
            resetPasswordUsecase.resetPassword().collect{ response ->
                when(response){
                    is Resource.Error -> {
                        _resetPassword.postValue(Event(response))
                    }
                    is Resource.Loading -> {
                        _resetPassword.postValue(Event(response))
                    }
                    is Resource.Success -> {
                        _resetPassword.postValue(Event(response))
                    }
                }

            }
        }

    }




    fun logoutReqDto() : LogoutReqDTO{

      return  LogoutReqDTO(   Settings.Secure.getString(
            appContext.contentResolver,
            Settings.Secure.ANDROID_ID
        ))


    }

    fun logout(context: Context) {

        viewModelScope.launch {
            GlideUtil.clearCache(context = appContext)
            deleteCache(context)
        }
        appConfig.clearAppConfigData()
        FirebaseMessaging.getInstance().deleteToken()

        viewModelScope.launch {
            localRepository.clearAllObjects()
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }





}