package com.sparkmembership.sparkowner.data.request


data class SignInReqDTO(
    val email: String = "",
    val password: String = "",
    val deviceID: String = "",
    val pushID: String = "",
)