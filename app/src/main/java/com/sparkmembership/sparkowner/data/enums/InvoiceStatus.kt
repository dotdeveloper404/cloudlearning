package com.sparkmembership.sparkowner.data.enums

enum class InvoiceStatus(val id: Int, val displayName: String) {
    ALL(0, "All Invoices"),
    PAID(1, "Paid Invoices"),
    UNPAID(2, "Unpaid Invoices")
}
