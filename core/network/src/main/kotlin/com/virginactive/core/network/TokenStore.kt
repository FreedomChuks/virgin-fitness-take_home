package com.virginactive.core.network

import com.virginactive.core.network.dto.TokenResponse
import java.time.Clock
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TokenStore {
    @Volatile
    var current: Tokens? = null
        private set

    private val _signedIn = MutableStateFlow(false)
    val signedIn: StateFlow<Boolean> = _signedIn.asStateFlow()

    fun <T> locked(block: () -> T): T = synchronized(this) { block() }

    fun save(tokens: Tokens) = locked {
        current = tokens
        _signedIn.value = true
    }

    fun clear() = locked {
        current = null
        _signedIn.value = false
    }
}

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Instant = Instant.MAX,
) {
    override fun toString(): String = "Tokens([REDACTED])"
}

internal val Tokens.bearer: String
    get() = "Bearer $accessToken"

internal fun TokenResponse.toTokens(clock: Clock): Tokens {
    require(accessToken.isNotBlank() && refreshToken.isNotBlank() && expiresIn >= 0)
    return Tokens(accessToken, refreshToken, clock.instant().plusSeconds(expiresIn))
}
