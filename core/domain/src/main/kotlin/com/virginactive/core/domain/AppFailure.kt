package com.virginactive.core.domain

data class AppFailure(
    val reason: Reason,
    val message: String,
    val requestId: String? = null,
    val code: Int? = null,
) {
    enum class Reason {
        NETWORK,
        UNAUTHORIZED,
        CONFLICT,
        INVALID,
        RATE_LIMITED,
        SERVER,
        UNKNOWN
    }
}
