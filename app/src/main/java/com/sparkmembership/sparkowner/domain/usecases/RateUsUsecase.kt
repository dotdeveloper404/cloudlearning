package com.sparkmembership.sparkowner.domain.usecases

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.RateUsReqDTO
import com.sparkmembership.sparkowner.data.response.RateUsResDTO
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RateUsUsecase @Inject constructor(
    private val mainRepository: MainRepository,
){
    suspend fun userRating(rateUsReqDTO: RateUsReqDTO) : Flow<Resource<RateUsResDTO>> = flow {
        try {
            emit(Resource.Loading())
            val rating = mainRepository.rating(rateUsReqDTO)
            if (rating. hasError == true) {
                emit(Resource.Error(rating.message))
            } else {
                emit(Resource.Success(rating))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
}