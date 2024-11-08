package com.sparkmembership.sparkowner.domain.usecases

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ResetPasswordUsecase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    suspend fun resetPassword(): Flow<Resource<BaseResponse>> = flow{
        try {
            emit(Resource.Loading())
            val response = authRepository.resetPassword()
            if (response. hasError == true) {
                emit(Resource.Error(response.message))
            } else {
                emit(Resource.Success(response))
            }

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
}