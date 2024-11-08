package com.sparkmembership.sparkowner.presentation.ui.contacts.AllContacts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.domain.usecases.contacts.AllContactsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import androidx.lifecycle.viewModelScope
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.data.remote.Event
import com.sparkmembership.sparkowner.data.response.AllContactsResDto
import com.sparkmembership.sparkowner.data.response.ContactsMetaTypeResDto
import com.sparkmembership.sparkowner.data.response.filter.ContactFilterResDTO
import com.sparkmembership.sparkowner.domain.repository.LocalRepository
import com.sparkmembership.sparkowner.domain.usecases.contacts.FilterContactsUsecase
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import com.sparkmembership.sparkowner.util.InternetManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val allContactsUseCase: AllContactsUseCase,
    private val filterContactsUsecase: FilterContactsUsecase,
    private val appConfig: AppConfig,
    private val localRepository: LocalRepository

) : BaseViewModel() {

    private val _allContacts = MutableLiveData<Event<Resource<AllContactsResDto?>>>()
    val allContacts: LiveData<Event<Resource<AllContactsResDto?>>> get() = _allContacts

    private val _contactTypesResDto = MutableLiveData<Resource<ContactsMetaTypeResDto?>>()
    val contactTypesResDto: LiveData<Resource<ContactsMetaTypeResDto?>> get() = _contactTypesResDto

    private var allContactsJob: Job? = null
    suspend fun getAllContacts(
        pageNum: Int ? = null,
        pageSize: Int? = null,
        contactType: String? = null,
        fTag: String? = null,
        fEliminateTags: String? = null,
        contactGroups: String? = null,
        fDOB: Long? = null,
        contactsEnteredStart: String? = null,
        contactsEnteredEnd: String? = null,
        StartAge: Long? = null,
        EndAge: Long? = null,
        search: String? = null,
        classRoster: String? = null,
        membership: String? = null,
        smsSubscribed: Boolean? = null

    ) {

        if (allContactsJob != null) allContactsJob?.cancel()


        if (!InternetManager.isInternetWorking()) {
            _allContacts.value = Event(Resource.Error(No_Internet_Connection))
            return
        }



        allContactsJob = allContactsUseCase.getContacts(
            pageNum = pageNum, pageSize = pageSize,contactType = contactType,
            fTag = fTag,fEliminateTags = fEliminateTags,contactGroups = contactGroups,
            fDOB = fDOB,contactsEnteredStart = contactsEnteredStart,
            contactsEnteredEnd = contactsEnteredEnd,StartAge = StartAge,EndAge = EndAge,
            search = search, classRoster = classRoster, membership = membership,
            smsSubscribed = smsSubscribed

        ).onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _allContacts.value = Event(result)
                }

                is Resource.Loading -> {
                    _allContacts.value = Event(result)

                }

                is Resource.Success ->  {
                    _allContacts.value = Event(result)
                }
            }
        }.launchIn(viewModelScope)
    }



    private var getContactTypesJob: Job? = null
    suspend fun getContactTypes() {
        if (getContactTypesJob != null) getContactTypesJob?.cancel()
        getContactTypesJob = allContactsUseCase.getContactTypes().onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _contactTypesResDto.value = result
                }

                is Resource.Loading -> {
                    _contactTypesResDto.value = result

                }

                is Resource.Success ->  {
                    _contactTypesResDto.value = result
                }
            }
        }.launchIn(viewModelScope)
    }



    private var _allContactFilters = MutableLiveData<Resource<ContactFilterResDTO>>()
    val allContactFilters: LiveData<Resource<ContactFilterResDTO>> get() = _allContactFilters

    var allContactFiltersJob: Job? = null
    suspend fun getAllContactFilters() {
        if (_allContactFilters.value != null) return
        if (allContactFiltersJob != null) allContactFiltersJob?.cancel()

        allContactFiltersJob = viewModelScope.launch {

            filterContactsUsecase.getAllContactFilters().collect { response ->

                when (response) {

                    is Resource.Loading -> {
                        _allContactFilters.postValue(response)
                    }

                    is Resource.Success -> {
                        _allContactFilters.postValue(response)
                        Log.e("veit", "observeFilterContactApi: ${response.data}", )

                    }

                    is Resource.Error -> {
                        _allContactFilters.postValue(response)
                    }

                }
            }
        }
    }





}