package com.sparkmembership.sparkowner.domain.usecases.contacts

import com.sparkmembership.sparkowner.constant.*
import com.sparkmembership.sparkowner.data.response.AllContactsResDto
import com.sparkmembership.sparkowner.data.response.ContactsMetaTypeResDto
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import com.sparkmembership.sparkowner.data.remote.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class AllContactsUseCase @Inject constructor(
    val mainRepository: MainRepository
) {
    suspend fun getContacts(
        pageNum: Int?= null,
        pageSize: Int?= null,
        contactType : String?= null,
        fTag : String?= null,
        fEliminateTags : String?= null,
        contactGroups : String?= null,
        fDOB : Long?= null,
        contactsEnteredStart : String?= null,
        contactsEnteredEnd : String?= null,
        StartAge : Long?= null,
        EndAge : Long? =null,
        search : String?= null,
        classRoster : String?= null,
        membership : String?= null,
        smsSubscribed : Boolean?= null

    ): Flow<Resource<AllContactsResDto?>> = flow {
        try {
            emit(Resource.Loading())
            val emojiResult = mainRepository.getContacts(
                pageSize = pageSize,
                pageNum = pageNum,
                contactType = contactType,
                fTag = fTag,
                fEliminateTags = fEliminateTags,
                contactGroups = contactGroups,
                fDOB = fDOB,
                contactsEnteredStart = contactsEnteredStart,
                contactsEnteredEnd = contactsEnteredEnd,
                StartAge = StartAge,
                EndAge = EndAge,
                search = search,
                classRoster = classRoster,
                membership = membership,
                smsSubscribed = smsSubscribed
            )

            if (emojiResult. hasError == true) {
                emit(Resource.Error(emojiResult.message))
            } else {
                emit(Resource.Success(emojiResult))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }


    suspend fun getContactTypes(): Flow<Resource<ContactsMetaTypeResDto?>> = flow {
        try {
            emit(Resource.Loading())
            val emojiResult = mainRepository.getContactTypes()

            if (emojiResult. hasError == true) {
                emit(Resource.Error(emojiResult.message))
            } else {
                emit(Resource.Success(emojiResult))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
}