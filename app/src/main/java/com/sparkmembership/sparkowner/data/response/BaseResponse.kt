package com.sparkmembership.sparkowner.data.response


open class  BaseResponse(open var message: String = "",
                         open val timestamp: String = "",
                         open val hasError: Boolean = false,
                         open val error: Any? = null)