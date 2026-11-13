package com.virginactive.core.network

import java.io.IOException

class ApiException(
    val status: Int,
    override val message: String,
    val requestId: String?,
) : Exception(message)

class ServerResponseException(override val message: String, cause: Throwable? = null) :
    IOException(message, cause)
