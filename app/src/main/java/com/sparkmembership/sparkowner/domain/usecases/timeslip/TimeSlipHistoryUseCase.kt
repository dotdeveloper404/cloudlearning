package com.sparkmembership.sparkowner.domain.usecases.timeslip

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ContactRequest
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.data.response.TimeSlip
import com.sparkmembership.sparkowner.data.response.TimeSlipHistoryDto
import com.sparkmembership.sparkowner.data.response.TimeSlipUserHistoryDto
import com.sparkmembership.sparkowner.data.response.UserTimeSlipDto
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TimeSlipHistoryUseCase @Inject constructor(
    val mainRepository: MainRepository
) {
    suspend fun timeSlipHistory(startDate: String?, endDate: String?, userName: String?, pageSize: Int?, pageIndex: Int?): Flow<Resource<TimeSlipHistoryDto>> = flow {
        try {
            emit(Resource.Loading())
            val getTimeSlipHistory = mainRepository.getTimeSlipHistory(pageSize = pageSize, pageIndex = pageIndex, userName = userName, startDate = startDate, endDate = endDate)
            if (getTimeSlipHistory.hasError == true) {
                emit(Resource.Error(getTimeSlipHistory.message))
            } else {
                emit(Resource.Success(getTimeSlipHistory))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }

    suspend fun timeSlipHistoryById(userId : Int,startDate: String?, endDate: String?, pageSize: Int?, pageIndex: Int?): Flow<Resource<TimeSlipUserHistoryDto>> = flow {
        try {
            emit(Resource.Loading())
            val getTimeSlipHistoryById = mainRepository.getTimeSlipHistoryById(pageSize = pageSize, pageIndex = pageIndex, id = userId, startDate = startDate, endDate = endDate)
            if (getTimeSlipHistoryById.hasError == true) {
                emit(Resource.Error(getTimeSlipHistoryById.message))
            } else {
                emit(Resource.Success(getTimeSlipHistoryById))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }

    suspend fun userTimeSlipHistory(): Flow<Resource<UserTimeSlipDto>> = flow {
        try {
            emit(Resource.Loading())
            val getUserTimeSlipHistory = mainRepository.getUserTimeSlipHistory()
            if (getUserTimeSlipHistory.hasError == true) {
                emit(Resource.Error(getUserTimeSlipHistory.message))
            } else {
                emit(Resource.Success(getUserTimeSlipHistory))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
}

