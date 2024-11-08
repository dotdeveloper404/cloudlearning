package com.sparkmembership.sparkowner.presentation.ui.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sparkmembership.sparkowner.data.entity.AddNoteEntity
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.AddNotesMetaDto
import com.sparkmembership.sparkowner.data.response.AllNotesDTO
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.domain.usecases.notes.NotesUseCase
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private var notesUseCase: NotesUseCase
) :BaseViewModel(){
    private val _allNotesDto = MutableLiveData<Resource<AllNotesDTO>>()
    val allNotesDto: LiveData<Resource<AllNotesDTO>> get() = _allNotesDto

    private val _deleteNoteDto = MutableLiveData<Resource<BaseResponse>>()
    val deleteNoteDto: LiveData<Resource<BaseResponse>> get() = _deleteNoteDto

    private val _addNoteDto = MutableLiveData<Resource<BaseResponse>>()
    val addNoteDto: LiveData<Resource<BaseResponse>> get() = _addNoteDto

    private val _addNotesMetaDataDto = MutableLiveData<Resource<AddNotesMetaDto>>()
    val addNotesMetaDataDto: LiveData<Resource<AddNotesMetaDto>> get() = _addNotesMetaDataDto

    private var allNotesJob: Job? = null
    suspend fun getAllNotes(
        contactId: Long,
        showSMSNotes: Boolean,
        showAllConnectedNotes: Boolean,
        completedOrderNotes: Boolean
    ) {

        if (allNotesJob != null) allNotesJob?.cancel()
        allNotesJob = notesUseCase.getAllNotes(contactId, showSMSNotes, showAllConnectedNotes, completedOrderNotes).onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _allNotesDto.value = result
                }

                is Resource.Loading -> {
                    _allNotesDto.value = result

                }

                is Resource.Success ->  {
                    _allNotesDto.value = result
                }
            }
        }.launchIn(viewModelScope)
    }

    private var deleteNoteJob: Job? = null
    suspend fun deleteNote(noteId: Int) {

        if (deleteNoteJob != null) deleteNoteJob?.cancel()
        deleteNoteJob = notesUseCase.deleteNote(noteId = noteId).onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _deleteNoteDto.value = result
                }

                is Resource.Loading -> {
                    _deleteNoteDto.value = result

                }

                is Resource.Success ->  {
                    _deleteNoteDto.value = result
                }
            }
        }.launchIn(viewModelScope)
    }

    private var addNoteMetaDataJob: Job? = null
    suspend fun getAddNotesMetaData() {

        if (addNoteMetaDataJob != null) addNoteMetaDataJob?.cancel()
        addNoteMetaDataJob = notesUseCase.getAddNoteMetaData().onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _addNotesMetaDataDto.value = result
                }

                is Resource.Loading -> {
                    _addNotesMetaDataDto.value = result

                }

                is Resource.Success ->  {
                    _addNotesMetaDataDto.value = result
                }
            }
        }.launchIn(viewModelScope)
    }

    private var addNoteJob: Job? = null
    suspend fun addNote(addNoteEntity: AddNoteEntity) {

        if (addNoteJob != null) addNoteJob?.cancel()
        addNoteJob = notesUseCase.addNote(addNoteEntity = addNoteEntity).onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _addNoteDto.value = result
                }

                is Resource.Loading -> {
                    _addNoteDto.value = result

                }

                is Resource.Success ->  {
                    _addNoteDto.value = result
                }
            }
        }.launchIn(viewModelScope)
    }
}