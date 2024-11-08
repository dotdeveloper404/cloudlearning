package com.sparkmembership.sparkowner.domain.usecases.notes

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.entity.AddNoteEntity
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.AddNotesMetaDto
import com.sparkmembership.sparkowner.data.response.AllNotesDTO
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.data.response.TimeSlipHistoryDto
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NotesUseCase @Inject constructor(
    val mainRepository: MainRepository
) {
    suspend fun getAllNotes(
        contactId: Long,
        showSMSNotes: Boolean,
        showAllConnectedNotes: Boolean,
        completedOrderNotes: Boolean
    ): Flow<Resource<AllNotesDTO>> = flow {
        try {
            emit(Resource.Loading())
            val getAllNotes = mainRepository.getAllNotes(contactId, showSMSNotes, showAllConnectedNotes, completedOrderNotes)
            if (getAllNotes.hasError == true) {
                emit(Resource.Error(getAllNotes.message))
            } else {
                emit(Resource.Success(getAllNotes))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }

    suspend fun deleteNote(
        noteId: Int
    ): Flow<Resource<BaseResponse>> = flow {
        try {
            emit(Resource.Loading())
            val deleteNote = mainRepository.deleteNotes(noteId = noteId)
            if (deleteNote.hasError == true) {
                emit(Resource.Error(deleteNote.message, deleteNote))
            } else {
                emit(Resource.Success(deleteNote))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }

    suspend fun getAddNoteMetaData(
    ): Flow<Resource<AddNotesMetaDto>> = flow {
        try {
            emit(Resource.Loading())
            val addNoteMetaData = mainRepository.getAddNotesMetaData()
            if (addNoteMetaData.hasError == true) {
                emit(Resource.Error(addNoteMetaData.message, addNoteMetaData))
            } else {
                emit(Resource.Success(addNoteMetaData))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
    suspend fun addNote(
        addNoteEntity: AddNoteEntity
    ): Flow<Resource<BaseResponse>> = flow {
        try {
            emit(Resource.Loading())
            val addNote = mainRepository.addNote(addNoteEntity = addNoteEntity)
            if (addNote.hasError == true) {
                emit(Resource.Error(addNote.message, addNote))
            } else {
                emit(Resource.Success(addNote))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }

}