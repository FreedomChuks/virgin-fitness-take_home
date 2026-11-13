package com.virginactive.core.data

import com.virginactive.core.domain.AppFailure
import com.virginactive.core.domain.Result
import com.virginactive.core.network.ApiException
import com.virginactive.core.network.ServerResponseException
import java.io.IOException
import java.time.format.DateTimeParseException
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

internal suspend fun <T> mapped(retry: Boolean = false, block: suspend () -> T): Result<T> =
    try {
        Result.Success(if (retry) retrying(block) else block())
    } catch (error: ApiException) {
        failure(error.reason(), error.message, error.requestId, error.status)
    } catch (error: ServerResponseException) {
        failure(AppFailure.Reason.SERVER, error.message)
    } catch (error: IOException) {
        failure(
            AppFailure.Reason.NETWORK,
            "We couldn't reach the server. Check your connection and try again.")
    } catch (error: IllegalArgumentException) {
        unreadable()
    } catch (error: DateTimeParseException) {
        unreadable()
    }

private fun failure(
    reason: AppFailure.Reason,
    message: String,
    requestId: String? = null,
    code: Int? = null,
) = Result.Failure(AppFailure(reason, message, requestId, code))

private fun unreadable() =
    failure(
        AppFailure.Reason.UNKNOWN, "The server returned data we couldn't read. Please try again.")

private fun ApiException.reason() =
    when (status) {
        401 -> AppFailure.Reason.UNAUTHORIZED
        409 -> AppFailure.Reason.CONFLICT
        400,
        403,
        404,
        422 -> AppFailure.Reason.INVALID
        429 -> AppFailure.Reason.RATE_LIMITED
        in 500..599 -> AppFailure.Reason.SERVER
        else -> AppFailure.Reason.UNKNOWN
    }

private val BACKOFF = listOf(500.milliseconds, 1_000.milliseconds)

private suspend fun <T> retrying(block: suspend () -> T): T {
    for (wait in BACKOFF) {
        try {
            return block()
        } catch (error: Exception) {
            if (!error.isTransient()) throw error
        }
        delay(wait)
    }
    return block()
}

private fun Exception.isTransient(): Boolean =
    this is IOException || (this is ApiException && (status == 429 || status in 500..599))
