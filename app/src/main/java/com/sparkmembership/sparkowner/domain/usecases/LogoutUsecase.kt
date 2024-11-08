package com.sparkmembership.sparkowner.domain.usecases

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.LogoutReqDTO
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class LogoutUsecase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend fun logout(
        logoutReqDTO: LogoutReqDTO
    ): Flow<Resource<BaseResponse>> = flow {
        try {
            emit(Resource.Loading())
            val logoutResponse = mainRepository.logout(logoutReqDTO)
            if (logoutResponse. hasError == true) {
                emit(Resource.Error(logoutResponse.message))
            } else {
                emit(Resource.Success(logoutResponse))
            }

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }

    }

}