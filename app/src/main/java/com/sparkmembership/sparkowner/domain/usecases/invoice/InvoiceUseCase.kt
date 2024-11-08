package com.sparkmembership.sparkowner.domain.usecases.invoice

import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.constant.Unexpected_error
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.AllNotesDTO
import com.sparkmembership.sparkowner.data.response.InvoiceDetailDto
import com.sparkmembership.sparkowner.data.response.InvoiceHistoryDto
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class InvoiceUseCase @Inject constructor(
    val mainRepository: MainRepository
)
{
    suspend fun getInvoiceHistory(
        contactId: Int?,
        invoiceType: Int?,
        startDate: String,
        endDate: String,
        pageIndex: Int?,
        pageSize: Int?
    ): Flow<Resource<InvoiceHistoryDto>> = flow {
        try {
            emit(Resource.Loading())
            val getInvoiceHistory = mainRepository.getInvoiceHistory(contactId = contactId, invoiceType = invoiceType, startDate = startDate, endDate = endDate, pageIndex = pageIndex, pageSize = pageSize)
            if (getInvoiceHistory.hasError == true) {
                emit(Resource.Error(getInvoiceHistory.message))
            } else {
                emit(Resource.Success(getInvoiceHistory))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }
    suspend fun getInvoiceDetail(
        invoiceID: Int
    ): Flow<Resource<InvoiceDetailDto>> = flow {
        try {
            emit(Resource.Loading())
            val getInvoiceDetail = mainRepository.getInvoiceDetail(invoiceID = invoiceID)
            if (getInvoiceDetail.hasError == true) {
                emit(Resource.Error(getInvoiceDetail.message))
            } else {
                emit(Resource.Success(getInvoiceDetail))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: Unexpected_error))
        } catch (e: IOException) {
            emit(Resource.Error(No_Internet_Connection))
        }
    }

}