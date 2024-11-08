package com.sparkmembership.sparkowner.data.response

data class StaffMemberTimeClockDto(
    val result : List<StaffMemberTimeClockResult>
): BaseResponse()

data class  StaffMemberTimeClockResult(
    val userID: Int,
    val firstName: String,
    val lastName: String,
    val timeIn: String,
    val timeOut: String
)