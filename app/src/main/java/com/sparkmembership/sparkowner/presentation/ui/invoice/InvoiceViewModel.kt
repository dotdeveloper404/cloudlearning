package com.sparkmembership.sparkowner.presentation.ui.invoice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.InvoiceHistoryDto
import com.sparkmembership.sparkowner.domain.usecases.invoice.InvoiceUseCase
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
                        private var invoiceUseCase: InvoiceUseCase
):BaseViewModel(){

    private val _invoiceHistoryDto = MutableLiveData<Resource<InvoiceHistoryDto>>()
    val invoiceHistory: LiveData<Resource<InvoiceHistoryDto>> get() = _invoiceHistoryDto

    private var invoiceHistoryJob: Job? = null
    suspend fun getInvoiceHistory(
        contactId: Int? = null,
        invoiceType: Int? = null,
        startDate: String,
        endDate: String,
        pageIndex: Int? = null,
        pageSize: Int? = null,
    ) {
        if (invoiceHistoryJob != null) invoiceHistoryJob?.cancel()
        invoiceHistoryJob = invoiceUseCase.getInvoiceHistory(contactId = contactId, invoiceType = invoiceType, startDate = startDate, endDate = endDate, pageIndex = pageIndex, pageSize).onEach { result ->
            when (result) {
                is Resource.Error ->{
                    _invoiceHistoryDto.value = result
                }

                is Resource.Loading -> {
                    _invoiceHistoryDto.value = result

                }

                is Resource.Success ->  {
                    _invoiceHistoryDto.value = result
                }
            }
        }.launchIn(viewModelScope)
    }
}