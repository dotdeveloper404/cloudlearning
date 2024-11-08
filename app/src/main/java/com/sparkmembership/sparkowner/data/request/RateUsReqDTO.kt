package com.sparkmembership.sparkowner.data.request

data class RateUsReqDTO(
    var whatsWrong : List<String>,
    val ratings: Int,
    val note: String,
)
