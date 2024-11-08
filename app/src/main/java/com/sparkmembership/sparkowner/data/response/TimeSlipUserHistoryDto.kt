package com.sparkmembership.sparkowner.data.response

import com.sparkmembership.sparkfitness.util.DateUtil.UTC_DATE_FORMAT
import com.sparkmembership.sparkfitness.util.DateUtil.calculateWorkHours
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

data class TimeSlipUserHistoryDto(
    val result: List<TimeSlipUserDetail>? = null
) : BaseResponse()

data class TimeSlipUserDetail(
    val currentPayPeriod: String,
    val totalTime: String,
    val timeSlip: MutableList<TimeSlip>
)

data class TimeSlip(
    val timeIn: String = "",
    var timeOut: String = "",
)

