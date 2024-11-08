package com.sparkmembership.sparkowner.config

import com.sparkmembership.sparkowner.constant.KEY_CONNECTED_LOCATION
import com.sparkmembership.sparkowner.constant.KEY_SIGN_DETAIL
import com.sparkmembership.sparkowner.data.response.ConnectedLocation
import com.sparkmembership.sparkowner.data.response.Result
import com.sparkmembership.sparkowner.domain.repository.LocalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppConfig @Inject constructor(val localRepository: LocalRepository) {

    var SIGN_IN : Result? = null
    var AUTH_TOKEN = ""
    var CONNECTED_LOCATION : ConnectedLocation? = null
    var IS_LOGGED_IN: Boolean = false



    init {
        initialize()
    }


    fun initialize() {
        CoroutineScope(Dispatchers.IO).launch {
            SIGN_IN  = localRepository.readObject<Result>(
                key = KEY_SIGN_DETAIL, Result::class.java
            ).stateIn(
                CoroutineScope(Dispatchers.IO)
            ).value

            CONNECTED_LOCATION = localRepository.readObject<ConnectedLocation>(
                key = KEY_CONNECTED_LOCATION, ConnectedLocation::class.java
            ).stateIn(
                CoroutineScope(Dispatchers.IO)
            ).value


            IS_LOGGED_IN = !SIGN_IN?.token?.accessToken.isNullOrEmpty()
            AUTH_TOKEN = SIGN_IN?.token?.accessToken ?: ""

        }



    }

    fun clearAppConfigData() {
        AUTH_TOKEN = ""
        CONNECTED_LOCATION = null
        SIGN_IN = null
        IS_LOGGED_IN = false
    }

}