package com.sparkmembership.sparkowner.presentation.ui.contacts.AddContacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sparkmembership.sparkowner.data.remote.Event
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ContactRequest
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.domain.usecases.contacts.AddContactsUseCase
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AddContactsViewModel @Inject constructor(
    private val addContactsUseCase: AddContactsUseCase
) : BaseViewModel(){
    private val _addContacts = MutableLiveData<Event<Resource<BaseResponse>>>()
    val addContacts: LiveData<Event<Resource<BaseResponse>>> get() = _addContacts


    private var addContactsJob: Job? = null
    suspend fun addContacts(contactRequest: ContactRequest) {
        if (addContactsJob != null) addContactsJob?.cancel()
        addContactsJob = addContactsUseCase.addContacts(contactRequest).onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _addContacts.postValue(Event(result))
                }

                is Resource.Loading -> {
                    _addContacts.postValue(Event(result))

                }

                is Resource.Success ->  {
                    _addContacts.postValue(Event(result))
                }
            }
        }.launchIn(viewModelScope)
    }
}