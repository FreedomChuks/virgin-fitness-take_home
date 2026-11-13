package com.virginactive.core.domain

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>

    data class Failure(val error: AppFailure) : Result<Nothing>
}

suspend fun <T, R> Result<T>.flatMap(transform: suspend (T) -> Result<R>): Result<R> =
    when (this) {
        is Result.Success -> transform(data)
        is Result.Failure -> this
    }
