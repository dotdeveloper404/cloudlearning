package com.sparkmembership.sparkowner.data.response


data class InvoiceDetailDto(
    val result: InvoiceResult,
):BaseResponse()

data class InvoiceResult(
    val invoiceID: Int,
    val contactName: String,
    val invoiceTime: String,
    val paymentDate: String,
    val paymentType: String,
    val paymentDescription: String,
    val subTotal: Double,
    val tax: Double,
    val discount: Double,
    val amountStillOwed: Double,
    val total: Double,
    val viewInvoiceLink: String,
    val editInvoiceLink: String,
    val invoiceItems: List<InvoiceItem>
)

data class InvoiceItem(
    val qty: Int,
    val itemName: String,
    val itemPrice: Double,
    val tax: Double
)

