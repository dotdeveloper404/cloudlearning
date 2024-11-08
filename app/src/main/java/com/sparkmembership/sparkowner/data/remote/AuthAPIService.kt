package com.sparkmembership.sparkowner.data.remote

import com.sparkmembership.sparkowner.data.request.ChangeConnectedLocationReqDTO
import com.sparkmembership.sparkowner.data.request.ForgotPasswordReqDTO
import com.sparkmembership.sparkowner.data.request.SignInReqDTO
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.data.response.SignInResDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthAPIService {

    @POST("auth/login")
    suspend fun signIn(@Body signInReqDTO: SignInReqDTO): Response<SignInResDto>

    @POST("auth/password/forget")
    suspend fun forgotPassword(@Body forgotPasswordReqDTO: ForgotPasswordReqDTO): Response<BaseResponse>

    @POST("auth/password/reset")
    suspend fun resetPassword(): Response<BaseResponse>

    @POST("auth/location/change")
    suspend fun changeConnectedLocation(@Body changeConnectedLocationReqDTO: ChangeConnectedLocationReqDTO): Response<SignInResDto>

}