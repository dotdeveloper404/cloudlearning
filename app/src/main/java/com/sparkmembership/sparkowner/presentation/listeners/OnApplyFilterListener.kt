package com.sparkmembership.sparkowner.presentation.listeners

import com.sparkmembership.sparkowner.data.request.ContactFilterResult

interface OnApplyFilterListener {

    fun onApplyFilter(contactFilterResult : ContactFilterResult)
    fun onResetFilter(contactFilterResult : ContactFilterResult)

}