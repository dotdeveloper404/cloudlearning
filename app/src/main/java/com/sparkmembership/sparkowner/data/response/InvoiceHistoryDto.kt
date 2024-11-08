package com.sparkmembership.sparkowner.data.response

data class InvoiceHistoryDto(
    val result :List<InvoiceHistory>
):BaseResponse()

data class InvoiceHistory(
    val locationID: Int,
    val invoiceID: Int,
    val contactName: String,
    val invoiceTime: String,
    val amountStillOwed: Double,
    val total: Double
)
