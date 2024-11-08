package com.sparkmembership.sparkowner.data.response

data class TimeSlipHistoryDto(
    val result: List<TimeSlipHistory>? = null
): BaseResponse()

data class TimeSlipHistory(
    val userID: Int,
    val userName: String,
    val totalTime: String
)