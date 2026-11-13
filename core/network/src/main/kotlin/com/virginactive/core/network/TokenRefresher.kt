package com.virginactive.core.network

import com.virginactive.core.network.dto.RefreshRequest
import com.virginactive.core.network.dto.TokenResponse
import java.io.IOException
import java.time.Clock
import java.time.Duration
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

internal class TokenRefresher(
    private val base: HttpUrl,
    private val tokens: TokenStore,
    private val refreshClient: OkHttpClient,
    private val json: Json,
    private val clock: Clock,
) {
    fun isExpiring(current: Tokens): Boolean =
        !clock.instant().isBefore(current.expiresAt.minus(EXPIRY_MARGIN))

    fun refreshIfExpiring(): Tokens? =
        tokens.locked {
            val latest = tokens.current ?: return@locked null
            if (!isExpiring(latest)) return@locked latest
            try {
                refresh(latest)
            } catch (_: IOException) {
                latest
            }
        }

    fun refresh(current: Tokens): Tokens? {
        val body =
            json
                .encodeToString(RefreshRequest.serializer(), RefreshRequest(current.refreshToken))
                .toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(base.resolve("auth/refresh")!!).post(body).build()
        refreshClient.newCall(request).execute().use { response ->
            if (response.code == 401) {
                tokens.clear()
                return null
            }
            if (!response.isSuccessful) {
                throw ServerResponseException("Couldn't refresh the session (${response.code}).")
            }
            val fresh =
                try {
                    json
                        .decodeFromString(TokenResponse.serializer(), response.body!!.string())
                        .toTokens(clock)
                } catch (error: IllegalArgumentException) {
                    throw ServerResponseException("The server returned an invalid session.", error)
                }
            tokens.save(fresh)
            return fresh
        }
    }

    private companion object {
        val EXPIRY_MARGIN: Duration = Duration.ofSeconds(30)
    }
}
