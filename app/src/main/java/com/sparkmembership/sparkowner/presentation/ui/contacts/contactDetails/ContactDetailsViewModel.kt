package com.sparkmembership.sparkowner.presentation.ui.contacts.contactDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.data.remote.Event
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ProfilePictureReqDTO
import com.sparkmembership.sparkowner.data.response.ContactDetailsResDTO
import com.sparkmembership.sparkowner.data.response.UploadProfilePictureResDTO
import com.sparkmembership.sparkowner.domain.usecases.contacts.ContactDetailsUsecase
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import com.sparkmembership.sparkowner.util.InternetManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel @Inject constructor(
    private val contactDetailsUsecase: ContactDetailsUsecase
) : BaseViewModel() {

    private val _contactDetail = MutableLiveData<Event<Resource<ContactDetailsResDTO>>>()
    val contactDetails: LiveData<Event<Resource<ContactDetailsResDTO>>> get() = _contactDetail

    var contactJob: Job? = null
    suspend fun getContactDetails(contactID: Long) {

        if (contactJob != null) {
            contactJob!!.cancel()
        }

        if (!InternetManager.isInternetWorking()) {
            _contactDetail.value = Event(Resource.Error(No_Internet_Connection))
            return
        }

        contactJob = viewModelScope.launch {
            contactDetailsUsecase.getContactDetails(contactID).collect { response ->

                when (response) {
                    is Resource.Error -> {
                        _contactDetail.value = Event(response)
                    }

                    is Resource.Loading -> {
                        _contactDetail.value = Event(response)
                    }

                    is Resource.Success -> {
                        _contactDetail.value = Event(response)
                    }
                }
            }

        }

    }



    private val _upoloadProfilePictureData = MutableLiveData<Event<Resource<UploadProfilePictureResDTO>>>()
    val uploadProfilePictureData: LiveData<Event<Resource<UploadProfilePictureResDTO>>> get() = _upoloadProfilePictureData


    private var uploadProfilePictureJob: Job? = null

    suspend fun uploadPicture(profilePictureReqDTO: ProfilePictureReqDTO, contactID: Long) {
        if (uploadProfilePictureJob != null) uploadProfilePictureJob?.cancel()

        uploadProfilePictureJob =
            contactDetailsUsecase.uploadContactDetailProfilePicture(profilePictureReqDTO, contactID).onEach { response ->
                when (response) {

                    is Resource.Error -> {
                        _upoloadProfilePictureData.value = Event(response)
                    }

                    is Resource.Loading -> {
                        _upoloadProfilePictureData.value = Event(response)                    }

                    is Resource.Success -> {
                        _upoloadProfilePictureData.value = Event(response)

                        val profilePicture = response.data?.result?.pictureURL
                        val profilePictureUpdatedTime =
                            response.data?.result?.profilePictureUpdatedTime



                    }
                }
            }.launchIn(viewModelScope)
    }

}