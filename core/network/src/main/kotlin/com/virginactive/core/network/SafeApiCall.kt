package com.virginactive.core.network

import com.virginactive.core.network.dto.ApiErrorDto
import kotlinx.serialization.json.Json
import retrofit2.Response

internal suspend fun <T> Json.safeApiCall(call: suspend () -> Response<T>): Response<T> {
    val response = call()
    if (!response.isSuccessful) throw response.toApiException(this)
    return response
}

internal fun <T> Response<T>.requiredBody(): T =
    body() ?: throw ServerResponseException("The server returned an empty response.")

internal fun Response<*>.toApiException(json: Json): ApiException {
    val error =
        errorBody()?.use { body ->
            runCatching { json.decodeFromString<ApiErrorDto>(body.string()) }.getOrNull()
        }
    return ApiException(
        code(),
        error?.message ?: "The server could not complete this request (${code()}).",
        error?.requestId ?: headers()["X-Request-ID"])
}
