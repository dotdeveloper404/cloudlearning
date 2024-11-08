package com.sparkmembership.sparkowner.data.repository

import com.google.gson.Gson
import com.sparkmembership.sparkfitness.data.repository.BaseRepository
import com.sparkmembership.sparkowner.data.remote.AuthAPIService
import com.sparkmembership.sparkowner.data.request.ChangeConnectedLocationReqDTO
import com.sparkmembership.sparkowner.data.request.ForgotPasswordReqDTO
import com.sparkmembership.sparkowner.data.request.SignInReqDTO
import com.sparkmembership.sparkowner.data.response.AllContactsResDto
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.data.response.SignInResDto
import com.sparkmembership.sparkowner.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authAPIService: AuthAPIService
) : AuthRepository, BaseRepository() {


    override suspend fun signIn(signInReqDTO: SignInReqDTO): SignInResDto {
        val signInCall = authAPIService.signIn(signInReqDTO)
        return when (val response = processCall { signInCall }) {
            is SignInResDto -> {
                response
            }

            else -> {
                Gson().fromJson(response.toString(), SignInResDto::class.java)

            }

        }
    }

    override suspend fun forgotPassword(forgotPasswordReqDTO: ForgotPasswordReqDTO): BaseResponse {

        val forgotPasswordCall = authAPIService.forgotPassword(forgotPasswordReqDTO)
        return when (val response = processCall { forgotPasswordCall }) {
            is BaseResponse -> {
                response
            }else -> {
                Gson().fromJson(response.toString(), BaseResponse::class.java)
            }            }
    }

    override suspend fun resetPassword(): BaseResponse {

        val resetPasswordCall = authAPIService.resetPassword()
        return when (val response = processCall { resetPasswordCall }) {
            is BaseResponse -> {
                response
            }

            else -> {
                Gson().fromJson(response.toString(), BaseResponse::class.java)
            }
        }
    }

    override suspend fun changeConnectedLocation(connectedLocationReqDTO: ChangeConnectedLocationReqDTO): SignInResDto {

        val changeConnectedLocationCall = authAPIService.changeConnectedLocation(connectedLocationReqDTO)
        return when (val response = processCall { changeConnectedLocationCall }) {
            is SignInResDto -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), SignInResDto::class.java)
            }
        }


    }


}