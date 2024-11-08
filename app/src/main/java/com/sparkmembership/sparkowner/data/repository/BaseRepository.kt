package com.sparkmembership.sparkfitness.data.repository

import android.util.Log
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

open class BaseRepository {


    suspend fun processCall(responseCall: suspend () -> Response<*>): Any? {
        return try {
            val response = responseCall.invoke()
            if (response.isSuccessful) {
                response.body()
            } else {
                response.errorBody()?.string()
            }
        } catch (e: Exception) {
            Log.d("internetissue--------->", e.message.toString())
            return e.message ?: e.toString()
        } catch (e: HttpException) {
            Log.d("internetissue--------->", e.message.toString())
            return e.message ?: e.toString()
        } catch (e: IOException) {
            Log.d("internetissue--------->", e.message.toString())
            return e.message ?: e.toString()
        } catch (timeout: SocketTimeoutException) {
            Log.d("timeout--------->", timeout.message.toString())
            return timeout.message ?: timeout.toString()
        }
    }
}