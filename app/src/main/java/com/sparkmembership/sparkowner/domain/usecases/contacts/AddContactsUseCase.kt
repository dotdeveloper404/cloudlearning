package com.sparkmembership.sparkowner.domain.usecases.contacts

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ContactRequest
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AddContactsUseCase  @Inject constructor(
    val mainRepository: MainRepository
)  {
    suspend fun addContacts(contactRequest: ContactRequest): Flow<Resource<BaseResponse>> = flow {
        try {
            emit(Resource.Loading())
            val addContacts = mainRepository.addContacts(contactRequest)
            if (addContacts. hasError == true) {
                emit(Resource.Error(addContacts.message))
            } else {
                emit(Resource.Success(addContacts))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
}