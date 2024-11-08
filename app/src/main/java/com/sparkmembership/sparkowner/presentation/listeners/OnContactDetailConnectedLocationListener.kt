package com.sparkmembership.sparkowner.presentation.listeners

import com.sparkmembership.sparkowner.data.response.Contacts

interface OnContactDetailConnectedLocationListener {

    fun onItemClick(contact: Contacts)

}