package com.sparkmembership.sparkowner.domain.usecases.whoisin

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.StaffMemberTimeClockDto
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WhoIsInUseCase @Inject constructor(
    val mainRepository: MainRepository
    ){
    suspend fun getStaffMemberTimeClock(date : String): Flow<Resource<StaffMemberTimeClockDto>> = flow {
        try {
            emit(Resource.Loading())
            val staffMemberTimeClock = mainRepository.getStaffMemberTimeClock(date)
            if (staffMemberTimeClock.hasError == true) {
                emit(Resource.Error(staffMemberTimeClock.message))
            } else {
                emit(Resource.Success(staffMemberTimeClock))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
}