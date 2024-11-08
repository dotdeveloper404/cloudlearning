package com.sparkmembership.sparkowner.data.response

data class AllContactsResDto(
    val result: List<Contact>,
) :BaseResponse()

data class Contact(
    val contactID: Int,
    val contactType: String,
    val emailAddress: String,
    val firstName: String,
    val howEntered: String,
    val lastName: String,
    val middleName: String,
    val mobilePhone: String,
    val phone: String,
    val picture: String,
    val profilePicLastUpdate: String
)