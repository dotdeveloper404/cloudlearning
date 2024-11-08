package com.sparkmembership.sparkowner.domain.usecases.contacts

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ProfilePictureReqDTO
import com.sparkmembership.sparkowner.data.response.ContactDetailsResDTO
import com.sparkmembership.sparkowner.data.response.UploadProfilePictureResDTO
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ContactDetailsUsecase @Inject constructor(
    private val mainRepository: MainRepository
) {

    suspend fun getContactDetails(contactID: Long): Flow<Resource<ContactDetailsResDTO>> = flow {
        try {
            emit(Resource.Loading())
            val contactDetailsResDTO = mainRepository.getContactDetails(contactID)
            if (contactDetailsResDTO.hasError == true) {
                emit(Resource.Error(contactDetailsResDTO.message))
            } else {
                emit(Resource.Success(contactDetailsResDTO))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }


    suspend fun uploadContactDetailProfilePicture(
        profilePictureReqDTO: ProfilePictureReqDTO,
        contactID: Long
    ): Flow<Resource<UploadProfilePictureResDTO>> = flow {
        try {
            emit(Resource.Loading())
            val uploadPicture = mainRepository.uploadContactDetailsPicture(profilePictureReqDTO, contactID)
            if (uploadPicture. hasError == true) {
                emit(Resource.Error(uploadPicture.message))
            } else {
                emit(Resource.Success(uploadPicture))
            }

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }

    }



}