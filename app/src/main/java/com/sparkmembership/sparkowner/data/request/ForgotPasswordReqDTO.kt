package com.sparkmembership.sparkowner.data.request

data class ForgotPasswordReqDTO(
    val email: String,
    val locationID: Int,
    val pushID : String
)
