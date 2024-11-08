package com.sparkmembership.sparkowner.data.response

data class ClockInDto (
    val result: ClockInResult
):BaseResponse()

data class ClockInResult (
    val timeSlipId : Int,
    val timeIn : String,
    )
