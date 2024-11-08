package com.sparkmembership.sparkowner.domain.repository

import com.sparkmembership.sparkowner.data.request.ChangeConnectedLocationReqDTO
import com.sparkmembership.sparkowner.data.request.ForgotPasswordReqDTO
import com.sparkmembership.sparkowner.data.request.SignInReqDTO
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.data.response.SignInResDto

interface AuthRepository {

    suspend fun signIn(signInReqDTO: SignInReqDTO): SignInResDto
    suspend fun forgotPassword(forgotPasswordReqDTO: ForgotPasswordReqDTO): BaseResponse
    suspend fun resetPassword(): BaseResponse
    suspend fun changeConnectedLocation(connectedLocationReqDTO: ChangeConnectedLocationReqDTO) : SignInResDto


}