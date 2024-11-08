package com.sparkmembership.sparkowner.domain.usecases.contacts

import com.sparkmembership.sparkowner.constant.*
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.filter.ContactFilterResDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class FilterContactsUsecase @Inject constructor(
    val mainRepository: MainRepository
) {


    suspend fun getAllContactFilters(): Flow<Resource<ContactFilterResDTO>> = flow {
        try {
            emit(Resource.Loading())
            val contactFilter = mainRepository.getAllContactFilters()
            if (contactFilter. hasError == true) {
                emit(Resource.Error(contactFilter.message))
            } else {
                emit(Resource.Success(contactFilter))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
}