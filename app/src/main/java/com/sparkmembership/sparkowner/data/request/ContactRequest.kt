package com.sparkmembership.sparkowner.data.request

data class ContactRequest(
    var contactType: String ?= null,
    var leadPhaseID: Int ?= null,
    var firstName: String ?= null,
    var middleName: String ?= null,
    var lastName: String ?= null,
    var dob: String ?= null,
    var gender: String? = null,
    var emailAddress: String ?= null,
    var mobilePhone: String ?= null,
    var tags: String?= null,
    var marketingSource: Int?= null,
    val marketingSourceDetail: String?= null,
    var referredBy: Int?= null
)
