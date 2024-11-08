package com.sparkmembership.sparkowner.domain.usecases.clockInAndOut

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.data.response.ClockInDto
import com.sparkmembership.sparkowner.data.response.ClockOutDto
import com.sparkmembership.sparkowner.data.response.TimeSlipHistoryDto
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ClockInAndOutUseCase @Inject constructor(
    val mainRepository: MainRepository
) {
    suspend fun clockIn(): Flow<Resource<ClockInDto>> = flow {
        try {
            emit(Resource.Loading())
            val clockIn = mainRepository.clockIn()
            if (clockIn.hasError == true) {
                emit(Resource.Error(clockIn.message))
            } else {
                emit(Resource.Success(clockIn))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }

    suspend fun clockOut(): Flow<Resource<ClockOutDto>> = flow {
        try {
            emit(Resource.Loading())
            val clockOut = mainRepository.clockOut()
            if (clockOut.hasError == true) {
                emit(Resource.Error(clockOut.message,data = clockOut))
            } else {
                emit(Resource.Success(clockOut))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
}