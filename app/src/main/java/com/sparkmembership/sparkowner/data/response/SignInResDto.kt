package com.sparkmembership.sparkowner.data.response

import com.sparkmembership.sparkowner.data.response.filter.Result


data class SignInResDto(
    val result: com.sparkmembership.sparkowner.data.response.Result,
):BaseResponse()


data class Result(
    val userDetails: UserDetails,
    val token: Token,
    val permissions: Permissions
)

data class UserDetails(
    val locationID: Long,
    val locationName: String,
    val firstName: String,
    val lastName: String,
    val isAccountBlockedFromFailAttempt: Boolean,
    val id: Long,
    val userImage: String,
    val picture: String,
    val apiKey: String,
    val allowSparktanAppClockIn: Boolean,
    val latitude: String,
    val longitude: String,
    val dateFormat: String,
    val allowSparktanAppLogin: Boolean,
    val profilePicLastUpdate: String,
    val connectedLocations: List<ConnectedLocation>
)

data class ConnectedLocation(
    val locationID: Long,
    val locationName: String,
    val userID: Long
)

data class Token(
    val accessToken: String,
    val accessTokenExpiration: String,
    val refreshToken: String
)

data class Permissions(
    val canPurchaseSMSCredits: Boolean,
    val canSendNotificationCompaigns: Boolean,
    val canViewPrintInvoice: Boolean,
    val canEditInvoiceDetails: Boolean,
    val canViewFinancials: Boolean,
    val canDeleteAppointments: Boolean,
    val canViewAllStaffMemberTasks: Boolean,
    val showDashboardStats: Boolean,
    val seePastDueStudentInformation: Boolean,
    val allowAccessToSparktanStaffApp: Boolean,
    val canEditTimeSlips: Boolean
)
