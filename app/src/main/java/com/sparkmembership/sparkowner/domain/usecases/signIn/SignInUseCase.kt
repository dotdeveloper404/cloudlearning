package com.sparkmembership.sparkowner.domain.usecases.signIn

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.request.SignInReqDTO
import com.sparkmembership.sparkowner.data.response.SignInResDto
import com.sparkmembership.sparkowner.domain.repository.AuthRepository
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ChangeConnectedLocationReqDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    val authRepository: AuthRepository
) {

    suspend fun signIn(signInReqDTO: SignInReqDTO): Flow<Resource<SignInResDto?>> = flow {
        try {
            emit(Resource.Loading())
            val signInResult = authRepository.signIn(signInReqDTO)
            if (signInResult. hasError == true) {
                emit(Resource.Error(signInResult.message))
            } else {
                emit(Resource.Success(signInResult))
            }

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }

    suspend fun changeConnectedLocation(changeConnectedLocationReqDTO: ChangeConnectedLocationReqDTO) : Flow<Resource<SignInResDto?>> = flow {
        try {
            emit(Resource.Loading())
            val signInResult = authRepository.changeConnectedLocation(changeConnectedLocationReqDTO)

            if (signInResult. hasError == true) {
                emit(Resource.Error(signInResult.message))
            } else {
                emit(Resource.Success(signInResult))
            }

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
}